package me.jiangcai.lib.sys.repository;

import me.jiangcai.lib.sys.entity.SystemString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 * @since 3.0
 */
public interface SystemStringRepository extends JpaRepository<SystemString, String>, JpaSpecificationExecutor<SystemString> {
}
