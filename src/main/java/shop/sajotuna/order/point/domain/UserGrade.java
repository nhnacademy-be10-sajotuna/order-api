package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.exception.NullValueException;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private GradePointPolicy grade;

    public void updateGrade(GradePointPolicy newGrade) {
        if (newGrade == null) {
            throw new NullValueException("새로운 등급은 null일 수 없습니다.");
        }

        if (newGrade.equals(grade)) {
            return;
        }
        this.grade = newGrade;
    }

    @Builder
    public UserGrade(Long id, Long userId, GradePointPolicy grade) {
        this.id = id;
        this.userId = userId;
        this.grade = grade;
    }

    public static UserGrade createForRegisterUser(Long userId, GradePointPolicy grade) {
        return UserGrade.builder()
                .userId(userId)
                .grade(grade).build();
    }
}
