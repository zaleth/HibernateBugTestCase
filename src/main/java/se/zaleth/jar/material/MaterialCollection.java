/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.jar.material;

import se.zaleth.jar.administration.UserGroup;

/**
 *
 * @author krister
 */
public class MaterialCollection {
    
    private long id;
    private MaterialCollection parentMaterialCollection;
    private String name;
    private UserGroup userGroup;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MaterialCollection getParentMaterialCollection() {
        return parentMaterialCollection;
    }

    public void setParentMaterialCollection(MaterialCollection parentMaterialCollection) {
        this.parentMaterialCollection = parentMaterialCollection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

}
