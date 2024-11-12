package com.tfx.layout.view;

import com.tfx.layout.interfaces.ResizeHandle;
import com.tfx.layout.manage.DialogManage;
import com.tfx.layout.manage.ResizeManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author tianfx
 * @date 2023/5/29 17:08
 */
public class TDialog extends JPanel implements ResizeHandle {
    
    private int zindex = 0;
    
    private int twidth = 500;
    private int theight = 300;
    private JPanel jPanel;
    
    private Color background = new Color(255,255,255,150);

    public static TDialog create(){
        return create(DialogManage.nextIndex(),500,300);
    }
    
    public static TDialog create(int twidth,int theight){
        return create(DialogManage.nextIndex(),twidth,theight);
    }

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public TDialog() {
    }

    public static TDialog create(int zindex,int twidth,int theight){
        TFrame tFrame = TFrame.getInstance();
        TDialog dialog = new TDialog();
        dialog.twidth = twidth;
        dialog.theight = theight;
        dialog.setBackground(dialog.background);
        dialog.zindex = zindex;
        dialog.setBounds(0,0, tFrame.getTwidth(),tFrame.getTheight());
        dialog.setLayout(null);
        JPanel jPanel = new JPanel();
        jPanel.setBounds((tFrame.getTwidth()-dialog.twidth)/2,(tFrame.getTheight()-dialog.theight)/2,dialog.twidth,dialog.theight);
        dialog.add(jPanel);
        tFrame.getContentPane().add(dialog);
        dialog.jPanel = jPanel;
        dialog.addColseListener();
        dialog.setVisible(false);
        DialogManage.register(dialog);
        ResizeManage.register(dialog);
        return dialog;
    }

    public int getZindex() {
        return zindex;
    }

    public JPanel getjPanel() {
        return jPanel;
    }

    public void addColseListener(){
        TDialog tmp = this;
        this.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                TFrame tFrame = TFrame.getInstance();
                int x = e.getX();int y = e.getY();
                int twidth = tFrame.getTwidth()/2;
                int theight = tFrame.getTheight()/2;
                int tw = tmp.twidth / 2;
                int th = tmp.theight / 2;
                if(twidth-tw > x || twidth+tw < x || theight - th > y || theight + th < y){
                    tmp.setVisible(false);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    @Override
    public void resizeFun(int x, int y) {
        JPanel jPanel = this.getjPanel();
        this.setBounds(0,0, x,y);
        jPanel.setBounds((x-this.twidth)/2,(y-this.theight)/2,twidth,theight);
    }
}
