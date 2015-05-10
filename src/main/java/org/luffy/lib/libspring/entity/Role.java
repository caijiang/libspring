/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.luffy.lib.libspring.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author luffy
 */
@Entity
@Table(name = "pRole", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Role implements Serializable {
    
    public static GrantedAuthority grantedAuthorityByRole(String role){
        return new SimpleGrantedAuthority(role);
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    /**
     * 所拥有的权利字符串 以,为分隔符
     */
    private String authorities;
    
    /**
     * 增加权限
     * @param authority 具体权限
     */
    public void addAuthority(String authority) {
        String cauths = this.getAuthorities();
        if(cauths==null)
            cauths = "";
        StringBuilder sb = new StringBuilder(cauths);
        if (sb.length()>0){
            sb.append(',');
        }
        sb.append(authority);
        this.setAuthorities(sb.toString());
    }
    
    public Collection<? extends GrantedAuthority> getGrantedAuthority(){
        if(this.getAuthorities()==null){
            return Collections.EMPTY_LIST;
        }
        ArrayList<GrantedAuthority> list  = new ArrayList();
        for(String s:this.getAuthorities().split(",")){
            list.add(Role.grantedAuthorityByRole(s));
        }
        return list;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }
}
