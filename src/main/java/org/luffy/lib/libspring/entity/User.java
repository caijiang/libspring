/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.luffy.lib.libspring.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 默认提供的用户体系设置AssociationOverrides可以修改表结构<p/>
 * TODO: 测试Transient于覆盖方法<p/>
 * @see javax.persistence.AssociationOverrides
 * @since 1.1
 * @author luffy
 */
@Entity
@Table(name = "pUser", uniqueConstraints = @UniqueConstraint(columnNames = {"loginName"}))
public class User implements UserDetails,Serializable{
    
    private static final long serialVersionUID = -349012453592429794L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String loginName;
    private String realName;
    //职务
    private String position;
    private String realEnglishName;
    private String password;
    private boolean accountNonExpired=true;
    private boolean accountNonLocked=true;
    private boolean credentialsNonExpired=true;        
    private boolean enabled=true;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;
    
    @ManyToOne
    private Role role;
        
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(role==null)
            return Collections.EMPTY_LIST;
        return role.getGrantedAuthority();
    }
        
    @Override
    public String getUsername(){
        return this.loginName;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", loginName=" + loginName + ", realName=" + realName + ", position=" + position + ", realEnglishName=" + realEnglishName + ", password=" + password + ", accountNonExpired=" + accountNonExpired + ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired=" + credentialsNonExpired + ", enabled=" + enabled + ", lastLogin=" + lastLogin + ", role=" + role +"}";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRealEnglishName() {
        return realEnglishName;
    }

    public void setRealEnglishName(String realEnglishName) {
        this.realEnglishName = realEnglishName;
    }

}
