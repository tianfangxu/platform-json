package com.tfx;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.tfx.mod.Node;
import com.tfx.mod.Pojo;
import com.tfx.utils.JSONToPojoUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tianfx
 * @date 2024/11/4 16:50
 */
public class MainUI implements ToolWindowFactory, DumbAware {
    public MainUI() {
    }

    private Project project;
    private Pojo root;

    public static void main(String[] args) {
        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JComponent jComponent = new MainUI().getJspo();

        jf.setContentPane(jComponent);
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
    
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            this.project = project;
            JComponent jComponent = getJspo();
            Content content = toolWindow.getContentManager().getFactory().createContent(jComponent, "Jspo", false);
            toolWindow.getContentManager().addContent(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JComponent getJspo(){
        JPanel panel = new JBPanel(null);
        panel.setPreferredSize(new Dimension(360,300));

        JButton transfer = new JButton("json预览");
        transfer.setLocation(5,5);
        transfer.setSize(120,26);

        JButton clear = new JButton("clear清除");
        clear.setLocation(130,5);
        clear.setSize(120,26);

        JBTextArea jsonArea = new JBTextArea();
        jsonArea.setLocation(5,40);
        jsonArea.setSize(360,240);
        JBScrollPane areaScrollPane = new JBScrollPane(jsonArea);
        areaScrollPane.setLocation(5,40);
        areaScrollPane.setSize(360,240);

        JLabel message = new JBLabel();
        message.setLocation(5,285);
        message.setForeground(JBColor.RED);

        transfer.addActionListener(getBuildJson(panel,jsonArea));
        clear.addActionListener(clearJson(panel,jsonArea));



        /* 添加 组件 到 内容面板 */
        panel.add(transfer);
        panel.add(clear);
        panel.add(areaScrollPane);
        panel.add(message);

        // 创建一个JScrollPane，并将JPanel作为参数传入

        return new JBScrollPane(panel);
    }

    private ActionListener clearJson(JPanel panel, JBTextArea jsonArea) {
        return (actionEvent)->{
            while (panel.getComponentCount() >= 5){
                panel.remove(4);
            }
            panel.setPreferredSize(new Dimension(360,300));
            JLabel message = (JLabel)panel.getComponent(3);
            message.setSize(0,0);
            jsonArea.setText("");
        };
    }

    public ActionListener getBuildJson(JPanel panel,JTextArea jsonArea){
        return (actionEvent)->{
            while (panel.getComponentCount() >= 5){
                panel.remove(4);
            }
            panel.setPreferredSize(new Dimension(360,300));
            JLabel message = (JLabel)panel.getComponent(3);
            message.setSize(0,0);
            String text = jsonArea.getText();
            try {
                root = JSONToPojoUtil.getPojoObj(text);
            }catch (Exception e){
                String msg = e.getMessage();
                message.setText(msg);
                message.setSize(360,40);
                return;
            }
            jsonArea.setText(JSONToPojoUtil.toJsonString(text));
            JButton create = new JButton("全部生成");
            create.setLocation(5,285);
            create.setSize(200,30);
            panel.add(create);

            JLabel notesLabel = new JBLabel("注释:");
            notesLabel.setLocation(220,285);
            notesLabel.setSize(35,30);
            panel.add(notesLabel);

            JBRadioButton yes = new JBRadioButton("y",true);
            yes.setLocation(260,285);
            yes.setSize(40,30);
            JBRadioButton no = new JBRadioButton("n");
            no.setLocation(300,285);
            no.setSize(40,30);
            ButtonGroup genderGroup = new ButtonGroup();
            genderGroup.add(yes);
            genderGroup.add(no);
            panel.add(yes);
            panel.add(no);

            create.addActionListener(getCreateListener(yes));

            Integer index = builderPanel(panel,yes, Collections.singletonList(root), 315);
            builderListener(root);
            panel.setPreferredSize(new Dimension(360,index+40));
            panel.repaint();
            panel.revalidate();
            Container parent = panel.getParent();
            parent.invalidate();
            parent.revalidate();
            parent.repaint();
        };
    }

    private ActionListener getCreateListener(JBRadioButton jbRadioButton) {
        return (actionEvent)->{
            PackageChooserDialog classPathChooser = new PackageChooserDialog("ClassPath Chooser", project);
            classPathChooser.show();
            PsiPackage selectedPackage = classPathChooser.getSelectedPackage();
            if (selectedPackage != null){
                PsiDirectory selectPath = getSelectPath(selectedPackage.getDirectories());
                resetVal(root);
                createAll(root,selectPath,jbRadioButton.isSelected());
            } 
        };
    }
    
    private void createAll(Pojo pojo,PsiDirectory selectPath,boolean isSelected){
        List<Node> fields = pojo.getFields();
        if (fields != null || fields.size() > 0){
            for (Node field : fields) {
                Pojo relation = field.getRelation();
                if (relation != null){
                    createAll(relation,selectPath,isSelected);
                }
            }
        }
        createJavaFileWithContent(selectPath,pojo.getName(),JSONToPojoUtil.writeClass(pojo,isSelected));
    }
    
    private void resetVal(Pojo pojo){
        pojo.setName(pojo.getNameJcp().getText());
        for (Node field : pojo.getFields()) {
            field.setKey(field.getKeyJcp().getText());
            field.setPre(field.getPreJcp().getText());
            if (field.getRelation()!=null) {
                resetVal(field.getRelation());
            }else{
                field.setType(field.getTypeJcp().getText());
            }
        }
    }

    public Integer builderPanel(JPanel panel,JBRadioButton jbRadioButton,List<Pojo> pojos,Integer index){
        if (pojos == null || pojos.size() == 0){
            return index;
        }
        List<Pojo> childs = new ArrayList<>();
        for (Pojo pojo : pojos) {
            JLabel classNameTip = new JBLabel("className:");
            classNameTip.setLocation(5,index);
            classNameTip.setSize(80,30);
            panel.add(classNameTip);

            JTextField className = new JBTextField(170);
            className.setLocation(90,index);
            className.setSize(170,30);
            className.setText(pojo.getName());
            panel.add(className);
            pojo.setNameJcp(className);

            JButton createSingle = new JButton("生成");
            createSingle.setLocation(265,index);
            createSingle.setSize(85,30);
            panel.add(createSingle);
            createSingle.addActionListener(e -> {
                PackageChooserDialog classPathChooser = new PackageChooserDialog("ClassPath Chooser", project);
                classPathChooser.show();
                PsiPackage selectedPackage = classPathChooser.getSelectedPackage();
                if (selectedPackage != null){
                    PsiDirectory selectPath = getSelectPath(selectedPackage.getDirectories());
                    resetVal(pojo);
                    createJavaFileWithContent(selectPath,pojo.getName(),JSONToPojoUtil.writeClass(pojo,jbRadioButton.isSelected()));
                }
            });

            index += 30;
            for (Node pojoField : pojo.getFields()) {
                
                JTextField col1 = new JBTextField(60);
                col1.setLocation(10,index);
                col1.setSize(60,30);
                col1.setText(pojoField.getPre());
                panel.add(col1);
                pojoField.setPreJcp(col1);

                JTextField col2 = new JBTextField(100);
                col2.setLocation(70,index);
                col2.setSize(100,30);
                col2.setText(pojoField.getCompleteType());
                col2.setEditable(pojoField.getRelation() == null);
                panel.add(col2);
                pojoField.setTypeJcp(col2);

                JTextField col3 = new JBTextField(150);
                col3.setLocation(170,index);
                col3.setSize(150,30);
                col3.setText(pojoField.getKey());
                panel.add(col3);
                pojoField.setKeyJcp(col3);

                JLabel deswc = new JBLabel("  example: "+(pojoField.getDesc()==null?"":pojoField.getDesc()));
                deswc.setLocation(330,index);
                deswc.setSize(300,30);
                panel.add(deswc);

                index += 30;
                if (pojoField.getRelation() != null){
                    childs.add(pojoField.getRelation());
                }
            }
        }
        if (childs.size() > 0) {
            JLabel dividerLabel = new JBLabel("———————————next———————————");
            dividerLabel.setLocation(0,index);
            dividerLabel.setSize(360,30);
            dividerLabel.setForeground(JBColor.RED);
            panel.add(dividerLabel);
        }
        return builderPanel(panel,jbRadioButton,childs,index+30);
    }



    private void builderListener(Pojo root) {
        List<Node> fields = root.getFields();
        for (Node field : fields) {
            Pojo relation = field.getRelation();
            if (relation == null) {
                continue;
            }
            JTextField nameJcp = relation.getNameJcp();
            nameJcp.getDocument().addDocumentListener(getdocListener(field));
            builderListener(relation);
        }
    }
    
    public DocumentListener getdocListener(Node field){
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // 当文本插入时调用
                processTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // 当文本删除时调用
                processTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // 当文本改变时调用（例如：更改了文本属性）
                processTextChanged();
            }
            private void processTextChanged() {
                Pojo relation = field.getRelation();
                JTextField nameJcp = relation.getNameJcp();
                String text = nameJcp.getText();
                field.setType(text);
                JTextField typeJcp = field.getTypeJcp();
                typeJcp.setText(field.getCompleteType());
            }
        };
    }
    
    public PsiDirectory getSelectPath(PsiDirectory[] directories){
        if (directories == null || directories.length == 0){
            return null;
        }
        if (directories.length == 1){
            return directories[0];
        }
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] activeFiles = fileEditorManager.getSelectedFiles();
        String activeModule = null;
        if (activeFiles.length > 0){
            Module module = ModuleUtilCore.findModuleForFile(activeFiles[0], project);
            assert module != null;
            activeModule = module.getName();
        }
        if (activeModule == null){
            return directories[0];
        }
        for (PsiDirectory directory : directories) {
            VirtualFile virtualFile = directory.getVirtualFile();
            Module module = ModuleUtilCore.findModuleForFile(virtualFile, project);
            assert module != null;
            if (activeModule.equals(module.getName())){
                return directory;
            }
        }
        return directories[0];
    }

    public void createJavaFileWithContent(PsiDirectory directory, String fileName, String fileContent) {
        WriteCommandAction.runWriteCommandAction(project,()->{
            // 优化导入和类引用
            JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(project);
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
            String javaFileContent = "package " + directory.getName() + ";\n\n" + fileContent;
            PsiJavaFile javaFile = (PsiJavaFile) psiFileFactory.createFileFromText(fileName + ".java", JavaFileType.INSTANCE, javaFileContent);
            codeStyleManager.optimizeImports(javaFile);
            codeStyleManager.shortenClassReferences(javaFile);
            // 将文件添加到指定目录
            directory.add(javaFile);
           
        });
    }
}
