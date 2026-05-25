package com.unab.dunab.service;

import com.unab.dunab.entity.*;
import com.unab.dunab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Servicio de Calculadora de Notas UNAB.
 *
 * Estructura de cálculo:
 * - 2 cortes, cada uno pesa 50% de la nota final
 * - Cada corte tiene N actividades con porcentajes que suman 100%
 * - Nota corte = suma(nota_actividad * porcentaje_actividad / 100)
 * - Nota final = (nota_corte1 * 0.5) + (nota_corte2 * 0.5)
 * - Para pasar: nota_final >= 3.0
 */
@Service
@RequiredArgsConstructor
public class GradeService {

    private final SubjectRepository subjectRepository;
    private final GradeEntryRepository gradeEntryRepository;
    private final UserRepository userRepository;

    /** Obtener todas las materias del usuario con sus notas calculadas */
    public List<Map<String, Object>> getUserSubjects(Long userId) {
        List<Subject> subjects = subjectRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Subject s : subjects) {
            result.add(buildSubjectDTO(s));
        }
        return result;
    }

    /** Crear una nueva materia */
    @Transactional
    public Map<String, Object> createSubject(Long userId, String name, Integer credits, String period) {
        User user = userRepository.findById(userId).orElseThrow();
        Subject subject = Subject.builder()
                .user(user).name(name).credits(credits)
                .academicPeriod(period).build();
        subject = subjectRepository.save(subject);
        return buildSubjectDTO(subject);
    }

    /** Eliminar materia */
    @Transactional
    public void deleteSubject(Long userId, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();
        if (!subject.getUser().getId().equals(userId)) throw new RuntimeException("No autorizado");
        gradeEntryRepository.deleteBySubjectId(subjectId);
        subjectRepository.delete(subject);
    }

    /** Agregar actividad a un corte */
    @Transactional
    public Map<String, Object> addGradeEntry(Long userId, Long subjectId,
                                              Integer corte, String activityName,
                                              Double percentage, Double grade) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();
        if (!subject.getUser().getId().equals(userId)) throw new RuntimeException("No autorizado");
        if (corte < 1 || corte > 2) throw new RuntimeException("El corte debe ser 1 o 2");
        if (percentage <= 0 || percentage > 100) throw new RuntimeException("El porcentaje debe estar entre 1 y 100");
        if (grade != null && (grade < 0 || grade > 5)) throw new RuntimeException("La nota debe estar entre 0.0 y 5.0");

        // Verificar que no se exceda el 100% del corte
        List<GradeEntry> existing = gradeEntryRepository.findBySubjectIdAndCorte(subjectId, corte);
        double totalPct = existing.stream().mapToDouble(GradeEntry::getPercentage).sum();
        if (totalPct + percentage > 100.01) {
            throw new RuntimeException("Los porcentajes del Corte " + corte + " superan el 100%. Disponible: " + Math.round(100 - totalPct) + "%");
        }

        GradeEntry entry = GradeEntry.builder()
                .subject(subject).corte(corte)
                .activityName(activityName).percentage(percentage)
                .grade(grade).graded(grade != null).build();
        gradeEntryRepository.save(entry);
        return buildSubjectDTO(subject);
    }

    /** Actualizar nota de una actividad */
    @Transactional
    public Map<String, Object> updateGrade(Long userId, Long entryId, Double grade) {
        GradeEntry entry = gradeEntryRepository.findById(entryId).orElseThrow();
        if (!entry.getSubject().getUser().getId().equals(userId)) throw new RuntimeException("No autorizado");
        if (grade != null && (grade < 0 || grade > 5)) throw new RuntimeException("La nota debe estar entre 0.0 y 5.0");
        entry.setGrade(grade);
        entry.setGraded(grade != null);
        gradeEntryRepository.save(entry);
        return buildSubjectDTO(entry.getSubject());
    }

    /** Eliminar actividad */
    @Transactional
    public Map<String, Object> deleteEntry(Long userId, Long entryId) {
        GradeEntry entry = gradeEntryRepository.findById(entryId).orElseThrow();
        Subject subject = entry.getSubject();
        if (!subject.getUser().getId().equals(userId)) throw new RuntimeException("No autorizado");
        gradeEntryRepository.delete(entry);
        return buildSubjectDTO(subject);
    }

    /** Construye el DTO completo de una materia con cálculos */
    private Map<String, Object> buildSubjectDTO(Subject subject) {
        List<GradeEntry> entries = gradeEntryRepository.findBySubjectIdOrderByCorteAscCreatedAtAsc(subject.getId());

        // Separar por corte
        List<Map<String, Object>> corte1Entries = new ArrayList<>();
        List<Map<String, Object>> corte2Entries = new ArrayList<>();

        double corte1Grade = 0, corte1PctDone = 0, corte1PctTotal = 0;
        double corte2Grade = 0, corte2PctDone = 0, corte2PctTotal = 0;

        for (GradeEntry e : entries) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", e.getId());
            dto.put("activityName", e.getActivityName());
            dto.put("percentage", e.getPercentage());
            dto.put("grade", e.getGrade());
            dto.put("graded", e.getGraded());
            // Aporte a la nota del corte
            double contribution = e.getGraded() && e.getGrade() != null
                    ? e.getGrade() * e.getPercentage() / 100 : 0;
            dto.put("contribution", Math.round(contribution * 100.0) / 100.0);

            if (e.getCorte() == 1) {
                corte1Entries.add(dto);
                corte1PctTotal += e.getPercentage();
                if (e.getGraded() && e.getGrade() != null) {
                    corte1Grade += contribution;
                    corte1PctDone += e.getPercentage();
                }
            } else {
                corte2Entries.add(dto);
                corte2PctTotal += e.getPercentage();
                if (e.getGraded() && e.getGrade() != null) {
                    corte2Grade += contribution;
                    corte2PctDone += e.getPercentage();
                }
            }
        }

        // Nota final actual (con lo calificado)
        double finalGrade = (corte1Grade * 0.5) + (corte2Grade * 0.5);

        // Proyección máxima posible
        double corte1Max = corte1Grade + ((100 - corte1PctDone) / 100.0 * 5.0);
        double corte2Max = corte2Grade + ((100 - corte2PctDone) / 100.0 * 5.0);
        double maxPossible = (corte1Max * 0.5) + (corte2Max * 0.5);

        // ¿Cuánto necesito en lo que falta para pasar?
        String neededInfo = calcNeeded(corte1Grade, corte1PctDone, corte1PctTotal,
                corte2Grade, corte2PctDone, corte2PctTotal);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", subject.getId());
        result.put("name", subject.getName());
        result.put("credits", subject.getCredits());
        result.put("academicPeriod", subject.getAcademicPeriod());
        result.put("status", subject.getStatus().name());
        result.put("corte1", Map.of(
                "entries", corte1Entries,
                "currentGrade", Math.round(corte1Grade * 100.0) / 100.0,
                "percentageDone", Math.round(corte1PctDone),
                "percentageTotal", Math.round(corte1PctTotal)
        ));
        result.put("corte2", Map.of(
                "entries", corte2Entries,
                "currentGrade", Math.round(corte2Grade * 100.0) / 100.0,
                "percentageDone", Math.round(corte2PctDone),
                "percentageTotal", Math.round(corte2PctTotal)
        ));
        result.put("finalGrade", Math.round(finalGrade * 100.0) / 100.0);
        result.put("maxPossible", Math.round(maxPossible * 100.0) / 100.0);
        result.put("passing", finalGrade >= 3.0);
        result.put("neededInfo", neededInfo);

        return result;
    }

    /**
     * Calcula cuánto necesita el estudiante en las actividades pendientes para pasar.
     * Nota mínima para pasar: 3.0
     * Fórmula: (nota_corte1 * 0.5) + (nota_corte2 * 0.5) >= 3.0
     */
    private String calcNeeded(double c1Grade, double c1Done, double c1Total,
                               double c2Grade, double c2Done, double c2Total) {
        boolean c1Complete = c1Done >= 99.9;
        boolean c2Complete = c2Done >= 99.9;

        if (c1Complete && c2Complete) {
            double final_ = (c1Grade * 0.5) + (c2Grade * 0.5);
            return final_ >= 3.0 ? "Materia aprobada. Nota final: " + String.format("%.2f", final_)
                    : "Materia perdida. Nota final: " + String.format("%.2f", final_);
        }

        // Calcular nota máxima posible del corte 1 si no está completo
        double c1Max = c1Grade + ((100 - c1Done) / 100.0 * 5.0);
        double c2Max = c2Grade + ((100 - c2Done) / 100.0 * 5.0);

        // Para pasar: (c1 * 0.5) + (c2 * 0.5) >= 3.0
        // => c1 * 0.5 + c2 * 0.5 >= 3.0
        if ((c1Max * 0.5) + (c2Max * 0.5) < 3.0) {
            return "Ya no es posible pasar esta materia aunque saques 5.0 en todo lo que falta.";
        }

        StringBuilder sb = new StringBuilder();

        if (!c1Complete) {
            double pctLeft = 100 - c1Done;
            // Necesito en el corte 1: para que corte1 me ayude a pasar
            // Supongamos que en corte 2 saco el máximo (5.0)
            // (c1 * 0.5) + (5.0 * 0.5) >= 3.0 => c1 >= 1.0
            double neededC1 = (3.0 - c2Max * 0.5) / 0.5;
            neededC1 = Math.max(0, Math.min(5.0, neededC1));
            // Cuánto necesito en las actividades pendientes del corte 1
            double neededInPending = (neededC1 - c1Grade) / (pctLeft / 100.0);
            neededInPending = Math.max(0, Math.min(5.0, neededInPending));
            sb.append("Corte 1 (queda ").append(Math.round(pctLeft)).append("%): necesitas mínimo ")
              .append(String.format("%.2f", neededInPending)).append(" en lo pendiente. ");
        }

        if (!c2Complete) {
            double pctLeft = 100 - c2Done;
            double neededC2 = (3.0 - c1Grade * 0.5) / 0.5;
            neededC2 = Math.max(0, Math.min(5.0, neededC2));
            double neededInPending = (neededC2 - c2Grade) / (pctLeft / 100.0);
            neededInPending = Math.max(0, Math.min(5.0, neededInPending));
            sb.append("Corte 2 (queda ").append(Math.round(pctLeft)).append("%): necesitas mínimo ")
              .append(String.format("%.2f", neededInPending)).append(" en lo pendiente.");
        }

        return sb.toString();
    }
}
