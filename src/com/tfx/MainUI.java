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
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
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
    
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            this.project = project;
            JComponent jComponent = getJspo();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(jComponent, "Jspo", false);
            toolWindow.getContentManager().addContent(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JComponent getJspo(){
        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(360,300));

        JButton transfer = new JButton("json预览");
        transfer.setLocation(5,5);
        transfer.setSize(120,26);

        JBTextArea jsonArea = new JBTextArea();
        jsonArea.setLocation(5,40);
        jsonArea.setSize(360,240);
        JBScrollPane areaScrollPane = new JBScrollPane(jsonArea);
        areaScrollPane.setLocation(5,40);
        areaScrollPane.setSize(360,240);

        JLabel message = new JLabel();
        message.setLocation(5,285);
        message.setForeground(JBColor.RED);

        transfer.addActionListener(getBuildJson(panel,jsonArea));



        /* 添加 组件 到 内容面板 */
        panel.add(transfer);
        panel.add(areaScrollPane);
        panel.add(message);

        // 创建一个JScrollPane，并将JPanel作为参数传入

        return new JBScrollPane(panel);
    }

    public ActionListener getBuildJson(JPanel panel,JTextArea jsonArea){
        return (actionEvent)->{
            while (panel.getComponentCount() >= 4){
                panel.remove(3);
            }
            panel.setPreferredSize(new Dimension(360,300));
            JLabel message = (JLabel)panel.getComponent(2);
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
            create.addActionListener(getCreateListener());
            panel.add(create);

            Integer index = builderPanel(panel, Collections.singletonList(root), 315);
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

    private ActionListener getCreateListener() {
        return (actionEvent)->{
            PackageChooserDialog classPathChooser = new PackageChooserDialog("ClassPath Chooser", project);
            classPathChooser.show();
            PsiPackage selectedPackage = classPathChooser.getSelectedPackage();
            if (selectedPackage != null){
                PsiDirectory selectPath = getSelectPath(selectedPackage.getDirectories());
                resetVal(root);
                createAll(root,selectPath);
            } 
        };
    }
    
    private void createAll(Pojo pojo,PsiDirectory selectPath){
        List<Node> fields = pojo.getFields();
        if (fields != null || fields.size() > 0){
            for (Node field : fields) {
                Pojo relation = field.getRelation();
                if (relation != null){
                    createAll(relation,selectPath);
                }
            }
        }
        createJavaFileWithContent(selectPath,pojo.getName(),JSONToPojoUtil.writeClass(pojo));
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

    public Integer builderPanel(JPanel panel,List<Pojo> pojos,Integer index){
        if (pojos == null || pojos.size() == 0){
            return index;
        }
        List<Pojo> childs = new ArrayList<>();
        for (Pojo pojo : pojos) {
            JLabel classNameTip = new JLabel("className:");
            classNameTip.setLocation(5,index);
            classNameTip.setSize(80,30);
            panel.add(classNameTip);

            JTextField className = new JTextField(170);
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
                    createJavaFileWithContent(selectPath,pojo.getName(),JSONToPojoUtil.writeClass(pojo));
                }
            });

            index += 30;
            for (Node pojoField : pojo.getFields()) {
                JTextField col1 = new JTextField(100);
                col1.setLocation(10,index);
                col1.setSize(100,30);
                col1.setText(pojoField.getPre());
                panel.add(col1);
                pojoField.setPreJcp(col1);

                JTextField col2 = new JTextField(100);
                col2.setLocation(110,index);
                col2.setSize(100,30);
                col2.setText(pojoField.getCompleteType());
                col2.setEditable(pojoField.getRelation() == null);
                panel.add(col2);
                pojoField.setTypeJcp(col2);

                JTextField col3 = new JTextField(150);
                col3.setLocation(210,index);
                col3.setSize(150,30);
                col3.setText(pojoField.getKey());
                panel.add(col3);
                pojoField.setKeyJcp(col3);

                index += 30;
                if (pojoField.getRelation() != null){
                    childs.add(pojoField.getRelation());
                }
            }
        }
        if (childs.size() > 0) {
            JLabel dividerLabel = new JLabel("———————————next———————————");
            dividerLabel.setLocation(0,index);
            dividerLabel.setSize(360,30);
            dividerLabel.setForeground(JBColor.RED);
            panel.add(dividerLabel);
        }
        return builderPanel(panel,childs,index+30);
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
