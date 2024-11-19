package com.tfx.mod;

import javax.swing.*;

/**
 * @author tianfx
 * @date 2024/11/7 10:47
 */
public class Node {

    private String pre = "private";
    
    private JTextField preJcp;

    private String typeFormat;

    private String type;

    private JTextField typeJcp;

    private String key;

    private JTextField keyJcp;

    private Pojo relation;
    
    private String desc;

    public Node(String type, String key,String typeFormat, Pojo relation) {
        this.type = type;
        this.key = key;
        this.typeFormat = typeFormat;
        this.relation = relation;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JTextField getPreJcp() {
        return preJcp;
    }

    public void setPreJcp(JTextField preJcp) {
        this.preJcp = preJcp;
    }

    public JTextField getTypeJcp() {
        return typeJcp;
    }

    public void setTypeJcp(JTextField typeJcp) {
        this.typeJcp = typeJcp;
    }

    public JTextField getKeyJcp() {
        return keyJcp;
    }

    public void setKeyJcp(JTextField keyJcp) {
        this.keyJcp = keyJcp;
    }

    public String getTypeFormat() {
        return typeFormat;
    }

    public void setTypeFormat(String typeFormat) {
        this.typeFormat = typeFormat;
    }

    public Pojo getRelation() {
        return relation;
    }

    public void setRelation(Pojo relation) {
        this.relation = relation;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Node{" +
                "pre='" + pre + '\'' +
                ", typeFormat='" + typeFormat + '\'' +
                ", type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", relation=" + relation +
                ", desc=" + desc +
                '}';
    }
    
    public String getCompleteType(){
        String type = this.getType();
        if (this.getTypeFormat()!=null){
            type = String.format(this.getTypeFormat(),type);
        }
        return type;
    }
}
