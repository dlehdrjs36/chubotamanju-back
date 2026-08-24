package com.side.chubotamanju.domain.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.side.chubotamanju.domain.user.entity.QSocialUser;
import com.side.chubotamanju.domain.user.entity.SocialUser;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class SocialUserRepositoryCustomImpl implements SocialUserRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 페이지 사이즈보다 데이터가 작거나, 마지막 페이지일 경우 카운트쿼리 미사용처리
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<SocialUser> searchPage(Pageable pageable) {
        List<SocialUser> contents = queryFactory
                .select(QSocialUser.socialUser)
                .from(QSocialUser.socialUser)
                .where()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(QSocialUser.socialUser.count())
                .from(QSocialUser.socialUser)
                .where();

        //new PageImpl<>(results, pageable, count);
        return PageableExecutionUtils.getPage(contents, pageable, () -> countQuery.fetchOne());
    }
}
