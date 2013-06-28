package fr.gael.ccsds.sip.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JList;
import javax.swing.JTable;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTree;

public class Main extends JFrame
{

   private JPanel contentPane;
   private JTable table_descriptors;

   /**
    * Launch the application.
    */
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            try
            {
               UIManager.setLookAndFeel(
                     UIManager.getSystemLookAndFeelClassName());
               Main frame = new Main();
               frame.setVisible(true);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      });
   }

   /**
    * Create the frame.
    */
   public Main()
   {
      setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/esa/pais/builder/Zip.png")));
      setTitle("SIP Builder 1.0.7 \u2013 My Project");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 540, 350);
      
      JMenuBar menuBar = new JMenuBar();
      setJMenuBar(menuBar);
      
      JMenu mnFile = new JMenu("File");
      mnFile.setMnemonic('F');
      menuBar.add(mnFile);
      
      JMenuItem mntmNew = new JMenuItem("New");
      mnFile.add(mntmNew);
      
      JMenuItem mntmOpen = new JMenuItem("Open...");
      mnFile.add(mntmOpen);
      
      JMenuItem mntmSave = new JMenuItem("Save");
      mntmSave.setEnabled(false);
      mnFile.add(mntmSave);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      contentPane.setLayout(new BorderLayout(0, 0));
      setContentPane(contentPane);
      
      JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      contentPane.add(tabbedPane, BorderLayout.CENTER);
      
      JPanel panel_project = new JPanel();
      tabbedPane.addTab("Projet", null, panel_project, null);
      
      JPanel panel_descriptors = new JPanel();
      panel_descriptors.setBorder(new TitledBorder(null, "Descriptors", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      
      table_descriptors = new JTable();
      
      JPanel panel_desc_buttons = new JPanel();
      GroupLayout gl_panel_descriptors = new GroupLayout(panel_descriptors);
      gl_panel_descriptors.setHorizontalGroup(
         gl_panel_descriptors.createParallelGroup(Alignment.TRAILING)
            .addGroup(gl_panel_descriptors.createSequentialGroup()
               .addContainerGap()
               .addComponent(table_descriptors, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
               .addPreferredGap(ComponentPlacement.UNRELATED)
               .addComponent(panel_desc_buttons, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
               .addContainerGap())
      );
      gl_panel_descriptors.setVerticalGroup(
         gl_panel_descriptors.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, gl_panel_descriptors.createSequentialGroup()
               .addContainerGap()
               .addGroup(gl_panel_descriptors.createParallelGroup(Alignment.TRAILING)
                  .addComponent(table_descriptors, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                  .addComponent(panel_desc_buttons, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 122, Short.MAX_VALUE))
               .addContainerGap())
      );
      
      JButton btnAdd = new JButton("Add...");
      
      JButton btnRemove = new JButton("Remove");
      btnRemove.setEnabled(false);
      GroupLayout gl_panel_desc_buttons = new GroupLayout(panel_desc_buttons);
      gl_panel_desc_buttons.setHorizontalGroup(
         gl_panel_desc_buttons.createParallelGroup(Alignment.LEADING)
            .addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
            .addComponent(btnRemove, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
      );
      gl_panel_desc_buttons.setVerticalGroup(
         gl_panel_desc_buttons.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_desc_buttons.createSequentialGroup()
               .addComponent(btnAdd)
               .addPreferredGap(ComponentPlacement.RELATED)
               .addComponent(btnRemove)
               .addGap(256))
      );
      panel_desc_buttons.setLayout(gl_panel_desc_buttons);
      panel_descriptors.setLayout(gl_panel_descriptors);
      
      JPanel panel_general = new JPanel();
      panel_general.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GroupLayout gl_panel_project = new GroupLayout(panel_project);
      gl_panel_project.setHorizontalGroup(
         gl_panel_project.createParallelGroup(Alignment.TRAILING)
            .addGroup(Alignment.LEADING, gl_panel_project.createSequentialGroup()
               .addContainerGap()
               .addGroup(gl_panel_project.createParallelGroup(Alignment.LEADING)
                  .addComponent(panel_descriptors, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                  .addComponent(panel_general, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE))
               .addContainerGap())
      );
      gl_panel_project.setVerticalGroup(
         gl_panel_project.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_project.createSequentialGroup()
               .addContainerGap()
               .addComponent(panel_general, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
               .addPreferredGap(ComponentPlacement.RELATED)
               .addComponent(panel_descriptors, GroupLayout.PREFERRED_SIZE, 171, Short.MAX_VALUE)
               .addContainerGap())
      );
      
      JLabel lblProject = new JLabel("Project ID:");
      
      JLabel lblNewLabel = new JLabel("<none>");
      GroupLayout gl_panel_general = new GroupLayout(panel_general);
      gl_panel_general.setHorizontalGroup(
         gl_panel_general.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_general.createSequentialGroup()
               .addContainerGap()
               .addComponent(lblProject)
               .addPreferredGap(ComponentPlacement.RELATED)
               .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
               .addContainerGap())
      );
      gl_panel_general.setVerticalGroup(
         gl_panel_general.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_general.createSequentialGroup()
               .addGap(5)
               .addGroup(gl_panel_general.createParallelGroup(Alignment.BASELINE)
                  .addComponent(lblNewLabel)
                  .addComponent(lblProject)))
      );
      panel_general.setLayout(gl_panel_general);
      panel_project.setLayout(gl_panel_project);
      
      JPanel panel_model = new JPanel();
      tabbedPane.addTab("Model", null, panel_model, null);
      
      JTree tree = new JTree();
      GroupLayout gl_panel_model = new GroupLayout(panel_model);
      gl_panel_model.setHorizontalGroup(
         gl_panel_model.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_model.createSequentialGroup()
               .addContainerGap()
               .addComponent(tree, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
               .addContainerGap())
      );
      gl_panel_model.setVerticalGroup(
         gl_panel_model.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_model.createSequentialGroup()
               .addContainerGap()
               .addComponent(tree, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
               .addContainerGap())
      );
      panel_model.setLayout(gl_panel_model);
      
      JPanel panel_sips = new JPanel();
      tabbedPane.addTab("SIPs", null, panel_sips, null);
      
      JPanel panel = new JPanel();
      
      JTree tree_1 = new JTree();
      GroupLayout gl_panel_sips = new GroupLayout(panel_sips);
      gl_panel_sips.setHorizontalGroup(
         gl_panel_sips.createParallelGroup(Alignment.TRAILING)
            .addGroup(gl_panel_sips.createSequentialGroup()
               .addContainerGap()
               .addComponent(tree_1, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
               .addPreferredGap(ComponentPlacement.UNRELATED)
               .addComponent(panel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
               .addContainerGap())
      );
      gl_panel_sips.setVerticalGroup(
         gl_panel_sips.createParallelGroup(Alignment.TRAILING)
            .addGroup(gl_panel_sips.createSequentialGroup()
               .addContainerGap()
               .addGroup(gl_panel_sips.createParallelGroup(Alignment.TRAILING)
                  .addComponent(tree_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                  .addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
               .addContainerGap())
      );
      
      JButton btnProduce = new JButton("Produce...");
      btnProduce.setToolTipText("Produce the selected SIPs");
      btnProduce.setEnabled(false);
      btnProduce.setHorizontalAlignment(SwingConstants.LEFT);
      
      JButton btnProduceAll = new JButton("Produce all...");
      btnProduceAll.setEnabled(false);
      btnProduceAll.setToolTipText("Produce all SIPs");
      btnProduceAll.setHorizontalAlignment(SwingConstants.LEFT);
      GroupLayout gl_panel = new GroupLayout(panel);
      gl_panel.setHorizontalGroup(
         gl_panel.createParallelGroup(Alignment.TRAILING)
            .addComponent(btnProduce, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
            .addComponent(btnProduceAll, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
      );
      gl_panel.setVerticalGroup(
         gl_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel.createSequentialGroup()
               .addComponent(btnProduce)
               .addPreferredGap(ComponentPlacement.RELATED)
               .addComponent(btnProduceAll)
               .addContainerGap(179, Short.MAX_VALUE))
      );
      panel.setLayout(gl_panel);
      panel_sips.setLayout(gl_panel_sips);
   }
}
