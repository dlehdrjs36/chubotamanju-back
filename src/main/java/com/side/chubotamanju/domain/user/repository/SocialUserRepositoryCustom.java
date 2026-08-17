package com.side.chubotamanju.domain.user.repository;


import com.side.chubotamanju.domain.user.entity.SocialUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SocialUserRepositoryCustom {

    Page<SocialUser> searchPage(Pageable pageable);
}
