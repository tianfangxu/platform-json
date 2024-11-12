package com.tfx.mod;

import javax.swing.*;
import java.util.List;

/**
 * @author tianfx
 * @date 2024/11/7 10:47
 */
public class Pojo {
    
    private String name;

    private JTextField nameJcp;

    private List<Node> fields;

    public Pojo(String name, List<Node> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getFields() {
        return fields;
    }

    public void setFields(List<Node> fields) {
        this.fields = fields;
    }

    public JTextField getNameJcp() {
        return nameJcp;
    }

    public void setNameJcp(JTextField nameJcp) {
        this.nameJcp = nameJcp;
    }


    @Override
    public String toString() {
        return "Pojo{" +
                ", name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }
}
