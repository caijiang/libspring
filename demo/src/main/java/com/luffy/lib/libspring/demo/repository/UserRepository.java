package com.luffy.lib.libspring.demo.repository;


import com.luffy.lib.libspring.demo.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by CJ on 5/12/15.
 *
 * @author CJ
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User,Long>{

    User findByLoginName(String name);

}
