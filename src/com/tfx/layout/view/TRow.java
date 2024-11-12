package com.tfx.layout.view;

import com.tfx.layout.interfaces.ResizeHandle;
import com.tfx.layout.manage.ResizeManage;
import com.tfx.layout.manage.TRowManage;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author tianfx
 * @date 2023/5/30 14:27
 */
public class TRow extends JPanel implements ResizeHandle {
    public static final int ROWSPAN = 16;
    private int id = 0;
    private int stheight = 80;
    private int theight = 80;
    private Container supperContainer;
    private List<TCol> tColList = new ArrayList<>();

    /**
     * 创建行
     * @param container
     * @param height
     * @return
     */
    public static TRow create(Container container,int height){
        int w = 0;
        if (Objects.isNull(container)){
            container = TFrame.getInstance().getContentPane();
            w= TFrame.getInstance().getTwidth();
        }else {
            w = container.getWidth();
        }
        TRow row = new TRow();
        row.theight = height;
        row.stheight = height;
        row.supperContainer = container;
        row.setLayout(null);
        row.setBorder(new LineBorder(Color.blue));
        int x = 0;int y = TRowManage.getY(row.supperContainer,null);
        row.setBounds(x,y,w,height);
        container.setLayout(null);
        container.add(row);
        TRowManage.register(row);
        ResizeManage.register(row);
        return row;
    }

    /**
     * 增加列
     * @param span
     * @return
     */
    public TCol add(int span){
        TCol tCol = TCol.create(this, span);
        int rowIndex = 0;
        int residue = 0;
        for (int i = 0; i < tColList.size(); i++) {
            TCol col = tColList.get(i);
            int colSpan = residue + col.getSpan();
            if (colSpan > ROWSPAN){
                rowIndex++;
                residue = col.getSpan();
            }else{
                residue = colSpan;
            }
        }
        if (tCol.getSpan()+residue > ROWSPAN){
            rowIndex++;
            residue = 0;
            stheight += theight;
        }
        
        this.setBounds(this.getBounds().x,this.getBounds().y,this.getWidth(),stheight);
        tCol.setBounds((this.getWidth()/ROWSPAN)*residue+1,rowIndex * theight+1,
                (this.getWidth()/ROWSPAN)*tCol.getSpan()-2,this.theight-2);
        this.add(tCol);
        tColList.add(tCol);
        return tCol;
    }

    @Override
    public void resizeFun(int x, int y) {
        int x1 = 0;int y1 = TRowManage.getY(this.supperContainer,this);
        int w1 = TFrame.getInstance().getTwidth();
        if (Objects.nonNull(this.supperContainer)&&!Objects.equals(this.supperContainer,TFrame.getInstance().getContentPane())){
            if (this.supperContainer instanceof ResizeHandle){
                ((ResizeHandle)this.supperContainer).resizeFun(x,y);
            }
            w1 = this.supperContainer.getWidth();
        }
        this.setBounds(x1,y1,w1,this.stheight);
        if (!tColList.isEmpty()){
            int rowIndex = 0;
            int residue = 0;
            for (int i = 0; i < tColList.size(); i++) {
                TCol col = tColList.get(i);
                if (residue + col.getSpan() > ROWSPAN){
                    rowIndex++;
                    residue = 0;
                }
                col.setBounds((this.getWidth()/ROWSPAN)*residue+1,rowIndex * theight+1,
                        (this.getWidth()/ROWSPAN)*col.getSpan()-2,this.theight-2);
                if (residue + col.getSpan() >= ROWSPAN){
                    rowIndex++;
                    residue = 0;
                }else{
                    residue = residue+col.getSpan();
                }
            }
        }
    }

    public int getStheight() {
        return stheight;
    }

    public Container getSupperContainer() {
        return supperContainer;
    }

    public void setId(int id) {
        this.id = id;
    }
}
