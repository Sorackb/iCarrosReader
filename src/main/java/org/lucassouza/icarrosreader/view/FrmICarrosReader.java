package org.lucassouza.icarrosreader.view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.lucassouza.icarrosreader.controller.Reader;
import org.lucassouza.icarrosreader.controller.Communicable;
import org.lucassouza.icarrosreader.controller.Comunicator;
import org.lucassouza.icarrosreader.type.ResourceType;
import static org.lucassouza.icarrosreader.type.ResourceType.BRAND;
import static org.lucassouza.icarrosreader.type.ResourceType.MODEL;

/**
 *
 * @author Lucas Bernardo [sorackb@gmail.com]
 */
public class FrmICarrosReader extends JFrame implements Communicable {

  private static final long serialVersionUID = 1;
  private final List<Integer> speedList = new ArrayList<>();
  private Long tick;
  private Integer yearByMinute;

  /**
   * Creates new form FrmLeitor
   */
  public FrmICarrosReader() {
    initComponents();
    this.pgbStep.setStringPainted(true);
    this.pgbBrand.setStringPainted(true);
    this.pgbModel.setStringPainted(true);
    this.pgbYear.setStringPainted(true);
    this.redrawnChart();
    Comunicator.getInstance().observe(this);
  }

  private void redrawnChart() {
    ChartPanel chartPanel;
    JFreeChart chart;
    XYSeriesCollection dataset;
    XYSeries xySeries = new XYSeries("Velocidade");

    for (int i = 0; i < this.speedList.size(); i++) {
      Long index = this.tick / 6L;

      if (index > 100L) {
        index = index - this.speedList.size() + i;
      } else {
        index = new Long(i + 1);
      }

      xySeries.add(index, this.speedList.get(i));
    }

    this.pnlChart.removeAll();
    this.pnlChart.revalidate();
    dataset = new XYSeriesCollection();
    dataset.addSeries(xySeries);
    chart = ChartFactory.createXYLineChart("Velocidade de leitura", "Minutos de execução",
            "Quantidade Ano/Modelo", dataset, PlotOrientation.VERTICAL, true,
            true, false);
    chart.removeLegend();
    chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(600, 300));
    this.pnlChart.setLayout(new BorderLayout());
    this.pnlChart.add(chartPanel, BorderLayout.CENTER);
  }

  private static void startSystemTray() {
    final FrmICarrosReader frame = new FrmICarrosReader();

    Image imgLogo = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(
            "resource/logo.png"));
    Image imgWindow = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(
            "resource/window.png"));
    frame.setIconImage(imgWindow);
    //Checa se há suporte a system tray
    if (!SystemTray.isSupported()) {
      System.out.println("SystemTray is not supported");
      return;
    }
    //Cria o popupmenu
    final PopupMenu popup = new PopupMenu();

    //Cria o icone da tray
    final TrayIcon trayIcon
            = new TrayIcon(imgLogo, frame.getTitle(), popup);
    final SystemTray tray = SystemTray.getSystemTray();

    // cria os itens do menu
    MenuItem aboutItem = new MenuItem("Abrir");
    MenuItem exitItem = new MenuItem("Sair");

    //Coloca os itens no menu
    popup.add(aboutItem);
    popup.addSeparator();
    popup.add(exitItem);

    //Adiciona o popup no tray
    trayIcon.setPopupMenu(popup);

    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      System.out.println("TrayIcon could not be added.");
    }

    //Cria o listener para abrir o jframe quando clicar
    trayIcon.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        if (frame.isVisible()) {
          frame.setVisible(false);
        } else {
          frame.setVisible(true);
        }
      }
    });
    //Cria o listener para esconder o jframe quando mandar fechar
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent evt) {
        String[] str = {"Sair", "Minimizar"};
        int result = JOptionPane.showOptionDialog(frame,
                "Você quer sair ou minimizar?", "Sair ou Minimizar?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, str, str[1]);
        if (result == 0) {
          frame.dispose();
          System.exit(0);
        } else {
          frame.setVisible(false);
        }//end else
      }//end windowClosing
    });//end WindowAdapter
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    pnlGeral = new javax.swing.JPanel();
    btnStart = new javax.swing.JToggleButton();
    pgbModel = new javax.swing.JProgressBar();
    pgbYear = new javax.swing.JProgressBar();
    lblYear = new javax.swing.JLabel();
    lblModel = new javax.swing.JLabel();
    lblBrand = new javax.swing.JLabel();
    lblAVG = new javax.swing.JLabel();
    pnlChart = new javax.swing.JPanel();
    pgbBrand = new javax.swing.JProgressBar();
    lblStep = new javax.swing.JLabel();
    pgbStep = new javax.swing.JProgressBar();

    setTitle("iCarros - Leitor");

    pnlGeral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    btnStart.setText("Iniciar");
    btnStart.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStartActionPerformed(evt);
      }
    });
    pnlGeral.add(btnStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, -1, -1));
    pnlGeral.add(pgbModel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 100, -1));
    pnlGeral.add(pgbYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 100, -1));

    lblYear.setText("Anos:");
    pnlGeral.add(lblYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, -1, -1));

    lblModel.setText("Modelos:");
    pnlGeral.add(lblModel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, -1, -1));

    lblBrand.setText("Marcas:");
    pnlGeral.add(lblBrand, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, -1, -1));
    pnlGeral.add(lblAVG, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 50, 63, 14));

    javax.swing.GroupLayout pnlChartLayout = new javax.swing.GroupLayout(pnlChart);
    pnlChart.setLayout(pnlChartLayout);
    pnlChartLayout.setHorizontalGroup(
      pnlChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    pnlChartLayout.setVerticalGroup(
      pnlChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 320, Short.MAX_VALUE)
    );

    pnlGeral.add(pnlChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 124, 560, 320));
    pnlGeral.add(pgbBrand, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 100, -1));

    lblStep.setText("Etapa:");
    pnlGeral.add(lblStep, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));
    pnlGeral.add(pgbStep, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 100, -1));

    getContentPane().add(pnlGeral, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
    Reader reader = new Reader();

    // Atualiza a métdia
    this.yearByMinute = 0;
    this.tick = 0L;
    new javax.swing.Timer(10000, (ActionEvent e) -> {
      Integer mediaMin = yearByMinute * 6;

      lblAVG.setText(mediaMin.toString() + "/min.");
      yearByMinute = 0;
      tick++;

      if ((tick % 6L) == 0L) {
        if (speedList.size() >= 100) {
          speedList.remove(0);
        }
        speedList.add(mediaMin);

        if ((tick / 6L) >= 2) {
          redrawnChart();
        }
      }
    }).start();

    reader.start();
    this.btnStart.setEnabled(false);
  }//GEN-LAST:event_btnStartActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(FrmICarrosReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(FrmICarrosReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(FrmICarrosReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(FrmICarrosReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        FrmICarrosReader.startSystemTray();
        //new FrmReader().setVisible(true);
      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JToggleButton btnStart;
  private javax.swing.JLabel lblAVG;
  private javax.swing.JLabel lblBrand;
  private javax.swing.JLabel lblModel;
  private javax.swing.JLabel lblStep;
  private javax.swing.JLabel lblYear;
  private javax.swing.JProgressBar pgbBrand;
  private javax.swing.JProgressBar pgbModel;
  private javax.swing.JProgressBar pgbStep;
  private javax.swing.JProgressBar pgbYear;
  private javax.swing.JPanel pnlChart;
  private javax.swing.JPanel pnlGeral;
  // End of variables declaration//GEN-END:variables

  @Override
  public void informAmount(ResourceType resourceType, Integer amount) {
    JProgressBar pgbRefresh = null;

    switch (resourceType) {
      case STEP:
        pgbRefresh = this.pgbStep;
        break;
      case BRAND:
        pgbRefresh = this.pgbBrand;
        break;
      case MODEL:
        pgbRefresh = this.pgbModel;
        break;
      case YEAR:
        pgbRefresh = this.pgbYear;
        break;
    }

    if (pgbRefresh != null) {
      pgbRefresh.setValue(0);
      pgbRefresh.setMinimum(0);
      pgbRefresh.setMaximum(amount);
    }
  }

  @Override
  public void informIncrement(ResourceType resourceType) {
    JProgressBar pgbRefresh = null;

    switch (resourceType) {
      case STEP:
        pgbRefresh = this.pgbStep;
        break;
      case BRAND:
        pgbRefresh = this.pgbBrand;
        break;
      case MODEL:
        pgbRefresh = this.pgbModel;
        break;
      case YEAR:
        this.yearByMinute++;
        pgbRefresh = this.pgbYear;
        break;
    }

    if (pgbRefresh != null) {
      pgbRefresh.setValue(pgbRefresh.getValue() + 1);
    }
  }

  @Override
  public void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    this.dispose();
    System.exit(0);
  }
  
  @Override
  public void finish() {
    this.dispose();
    System.exit(0);
  }
}
