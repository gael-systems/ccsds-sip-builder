package fr.gael.ccsds.sip.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import javax.swing.JSplitPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import javax.swing.JList;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;

public class CollectorDialog extends JDialog
{
   private JTable table;

   /**
    * Launch the application.
    */
   public static void main(String[] args)
   {
      try
      {
         UIManager.setLookAndFeel(
               UIManager.getSystemLookAndFeelClassName());
         CollectorDialog dialog = new CollectorDialog();
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         dialog.setVisible(true);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Create the dialog.
    */
   public CollectorDialog()
   {
      setTitle("Collector \u2013 GroupType_ID");
      setIconImage(Toolkit.getDefaultToolkit().getImage(CollectorDialog.class.getResource("/esa/pais/builder/Zip.png")));
      setBounds(100, 100, 450, 300);
      
      JPanel panel = new JPanel();
      panel.setBorder(new TitledBorder(null, "Rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      
      JPanel panel_2 = new JPanel();
      
      table = new JTable();
      GroupLayout gl_panel = new GroupLayout(panel);
      gl_panel.setHorizontalGroup(
         gl_panel.createParallelGroup(Alignment.TRAILING)
            .addGroup(gl_panel.createSequentialGroup()
               .addContainerGap()
               .addComponent(table, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
               .addPreferredGap(ComponentPlacement.RELATED)
               .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
      );
      gl_panel.setVerticalGroup(
         gl_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
               .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
                  .addComponent(panel_2, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                  .addComponent(table, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
               .addContainerGap())
      );
      gl_panel.setAutoCreateGaps(true);
      gl_panel.setAutoCreateContainerGaps(true);
      
      JButton btnNewButton = new JButton("Add");
      btnNewButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      
      JButton btnRemove = new JButton("Remove");
      btnRemove.setEnabled(false);
      GroupLayout gl_panel_2 = new GroupLayout(panel_2);
      gl_panel_2.setHorizontalGroup(
         gl_panel_2.createParallelGroup(Alignment.LEADING)
            .addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
            .addComponent(btnRemove, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
      );
      gl_panel_2.setVerticalGroup(
         gl_panel_2.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_2.createSequentialGroup()
               .addComponent(btnNewButton)
               .addPreferredGap(ComponentPlacement.RELATED)
               .addComponent(btnRemove)
               .addContainerGap(100, Short.MAX_VALUE))
      );
      panel_2.setLayout(gl_panel_2);
      panel.setLayout(gl_panel);
      
      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      
      JList list = new JList();
      GroupLayout gl_panel_1 = new GroupLayout(panel_1);
      gl_panel_1.setHorizontalGroup(
         gl_panel_1.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panel_1.createSequentialGroup()
               .addContainerGap()
               .addComponent(list, GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
               .addContainerGap())
      );
      gl_panel_1.setVerticalGroup(
         gl_panel_1.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
               .addContainerGap()
               .addComponent(list, GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
               .addContainerGap())
      );
      panel_1.setLayout(gl_panel_1);
      GroupLayout groupLayout = new GroupLayout(getContentPane());
      groupLayout.setHorizontalGroup(
         groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
               .addContainerGap()
               .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                  .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                  .addComponent(panel, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
               .addGap(12))
      );
      groupLayout.setVerticalGroup(
         groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
               .addContainerGap()
               .addComponent(panel, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
               .addPreferredGap(ComponentPlacement.RELATED)
               .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
               .addContainerGap())
      );
      getContentPane().setLayout(groupLayout);
   }
}
