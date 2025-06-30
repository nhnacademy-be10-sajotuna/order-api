package shop.sajotuna.order.point.repository;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.point.domain.GradePointPolicy;
import shop.sajotuna.order.point.domain.QGradePointPolicy;

@Repository
@Transactional(readOnly = true)
public class GradePointPolicyQueryRepositoryImpl extends QuerydslRepositorySupport implements GradePointPolicyQueryRepository {

    public GradePointPolicyQueryRepositoryImpl() {
        super(GradePointPolicy.class);
    }

    @Override
    public GradePointPolicy findApplicablePolicy(int totalAmount) {
        QGradePointPolicy gradePointPolicy = QGradePointPolicy.gradePointPolicy;
        return from(gradePointPolicy)
                .where(gradePointPolicy.minTotalOrderPrice.amount.loe(totalAmount))
                .orderBy(gradePointPolicy.minTotalOrderPrice.amount.desc())
                .select(gradePointPolicy)
                .fetchFirst();
    }
}
