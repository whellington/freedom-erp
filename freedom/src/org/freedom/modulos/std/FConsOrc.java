/**
 * @version 02/08/2003 <BR>
 * @author Setpoint Inform�tica Ltda./Fernando Oliveira da Silva <BR>
 *
 * Projeto: Freedom <BR>
 *  
 * Pacote: org.freedom.modulos.std <BR>
 * Classe: @(#)FConsOrc.java <BR>
 * 
 * Este programa � licenciado de acordo com a LPG-PC (Licen�a P�blica Geral para Programas de Computador), <BR>
 * vers�o 2.1.0 ou qualquer vers�o posterior. <BR>
 * A LPG-PC deve acompanhar todas PUBLICA��ES, DISTRIBUI��ES e REPRODU��ES deste Programa. <BR>
 * Caso uma c�pia da LPG-PC n�o esteja dispon�vel junto com este Programa, voc� pode contatar <BR>
 * o LICENCIADOR ou ent�o pegar uma c�pia em: <BR>
 * Licen�a: http://www.lpg.adv.br/licencas/lpgpc.rtf <BR>
 * Para poder USAR, PUBLICAR, DISTRIBUIR, REPRODUZIR ou ALTERAR este Programa � preciso estar <BR>
 * de acordo com os termos da LPG-PC <BR> <BR>
 *
 * Formul�rio de consulta de or�amento.
 * 
 */

package org.freedom.modulos.std;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.freedom.bmps.Icone;
import org.freedom.componentes.GuardaCampo;
import org.freedom.componentes.ImprimeOS;
import org.freedom.componentes.JCheckBoxPad;
import org.freedom.componentes.JRadioGroup;
import org.freedom.componentes.JTextFieldFK;
import org.freedom.componentes.JTextFieldPad;
import org.freedom.componentes.ListaCampos;
import org.freedom.componentes.Painel;
import org.freedom.componentes.Tabela;
import org.freedom.funcoes.Funcoes;
import org.freedom.telas.Aplicativo;
import org.freedom.telas.FFilho;
import org.freedom.telas.FPrincipal;

public class FConsOrc extends FFilho implements ActionListener {
  private Painel pinCab = new Painel(0, 200);
  private JPanel pnCli = new JPanel(new BorderLayout());
  private JPanel pnRod = new JPanel(new BorderLayout());
  private JTextFieldPad txtCodCli = new JTextFieldPad(JTextFieldPad.TP_INTEGER, 8, 0);
  private JTextFieldFK txtNomeCli = new JTextFieldFK(JTextFieldPad.TP_STRING, 50, 0);

  private JTextFieldPad txtValorMenor = new JTextFieldPad(JTextFieldPad.TP_DECIMAL, 9, 2);
  private JTextFieldPad txtValorMaior = new JTextFieldPad(JTextFieldPad.TP_DECIMAL, 9, 2);

  private JTextFieldPad txtDtIni = new JTextFieldPad(JTextFieldPad.TP_DATE, 10, 0);
  private JTextFieldPad txtDtFim = new JTextFieldPad(JTextFieldPad.TP_DATE, 10, 0);
  private JCheckBoxPad cbAberto = new JCheckBoxPad("Aberto", "S", "N");
  private JCheckBoxPad cbCompleto = new JCheckBoxPad("Completo", "S", "N");
  private JCheckBoxPad cbLiberado = new JCheckBoxPad("Liberado", "S", "N");
  private JCheckBoxPad cbFaturado = new JCheckBoxPad("Faturado", "S", "N");

  private JRadioGroup gbVenc;

  private Tabela tab = new Tabela();
  private JButton btBusca = new JButton("Buscar", Icone.novo("btPesquisa.gif"));
  private JButton btPrevimp = new JButton("Imprimir", Icone.novo("btPrevimp.gif"));
  private JButton btConsVenda = new JButton(Icone.novo("btSaida.gif"));
  JButton btSair = new JButton("Sair",Icone.novo("btSair.gif"));
  private JScrollPane spnTab = new JScrollPane(tab);

  private ListaCampos lcCli = new ListaCampos(this, "CL");
  private Connection con = null;
  private FPrincipal fPrim;
  public FConsOrc() {
    setTitulo("Pesquisa Or�amentos");
    setAtribos(10, 10, 514, 480);

    System.out.println("� esse mesmo");

    
    txtDtIni.setRequerido(true);
    txtDtFim.setRequerido(true);
    txtCodCli.setTabelaExterna(lcCli);
    txtCodCli.setNomeCampo("CodCli");
    txtCodCli.setFK(true);
    lcCli.setReadOnly(true);
    lcCli.montaSql(false, "CLIENTE", "VD");

    lcCli.add(new GuardaCampo(txtCodCli, 7, 100, 80, 20, "CodCli", "C�d.", true, false, null, JTextFieldPad.TP_INTEGER, false), "txtCodCli");
    lcCli.add(new GuardaCampo(txtNomeCli, 90, 100, 207, 20, "NomeCli", "Nome", false, false, null, JTextFieldPad.TP_STRING, false), "txtNomeCli");
    txtCodCli.setTabelaExterna(lcCli);
    txtCodCli.setNomeCampo("CodCli");
    txtCodCli.setFK(true);
    lcCli.setReadOnly(true);
    lcCli.montaSql(false, "CLIENTE", "VD");

    //		txtDescTipoConv.setListaCampos(lcTipoConv);
    //		lcTipoConv.setQueryCommit(false);

    Container c = getTela();
    c.add(pnRod, BorderLayout.SOUTH);
    c.add(pnCli, BorderLayout.CENTER);
    pnCli.add(pinCab, BorderLayout.NORTH);
    pnCli.add(spnTab, BorderLayout.CENTER);
    
    btSair.setPreferredSize(new Dimension(100,30));
    btConsVenda.setPreferredSize(new Dimension(30,30));
    
    JPanel pnBordaSair = new JPanel(new FlowLayout(FlowLayout.CENTER,3,3));
    pnBordaSair.add(btSair);
    pnRod.add(pnBordaSair,BorderLayout.EAST);
    JPanel pnBordaConsVenda = new JPanel(new FlowLayout(FlowLayout.CENTER,3,3));
    pnBordaConsVenda.add(btConsVenda);
    pnRod.add(pnBordaConsVenda,BorderLayout.WEST);
  
    Vector vVals = new Vector();
    vVals.addElement("D");
    vVals.addElement("V");
    Vector vLabs = new Vector();
    vLabs.addElement("Data de Emiss�o");
    vLabs.addElement("Data de Validade");
    gbVenc = new JRadioGroup(2, 1, vLabs, vVals);
    JLabel lbLinha = new JLabel();
    lbLinha.setBorder(BorderFactory.createEtchedBorder());
    JLabel lbLinha2 = new JLabel();
    lbLinha2.setBorder(BorderFactory.createEtchedBorder());
    JLabel lbStatus = new JLabel(" Status:");
    lbStatus.setOpaque(true);
    JLabel lbFiltrar = new JLabel(" Filtrar:");
    lbFiltrar.setOpaque(true);

    pinCab.adic(new JLabel("Per�odo:"), 7, 5, 50, 20);
    pinCab.adic(txtDtIni, 7, 25, 100, 20);
    pinCab.adic(new JLabel("At�"), 110, 25, 27, 20);
    pinCab.adic(txtDtFim, 137, 25, 100, 20);

   // pinCab.adic(lbLinha, 60, 15, 315, 2);
    pinCab.adic(new JLabel("C�digo e nome do cliente"), 7, 48, 250, 20);
    pinCab.adic(txtCodCli, 7, 70, 70, 20);
    pinCab.adic(txtNomeCli, 80, 70, 410, 20);

    pinCab.adic(new JLabel("Valores :  "), 250, 5, 250, 20);
    pinCab.adic(txtValorMenor, 250, 25, 100, 20);
    pinCab.adic(new JLabel("at�  "), 360, 25, 27, 20);
    pinCab.adic(txtValorMaior, 390, 25, 100, 20);

    pinCab.adic(lbStatus, 15, 95, 50, 18);
    pinCab.adic(lbLinha2, 7, 105, 201, 70);
    pinCab.adic(cbAberto, 15, 117, 80, 20);
    pinCab.adic(cbCompleto, 15, 142, 80, 20);
    pinCab.adic(cbLiberado, 110, 117, 80, 20);
    pinCab.adic(cbFaturado, 110, 142, 80, 20);

    pinCab.adic(lbFiltrar, 220, 95, 50, 18);
    pinCab.adic(gbVenc, 213, 105, 164, 70);

    pinCab.adic(btBusca, 382, 110, 110, 30);
    pinCab.adic(btPrevimp, 382, 145, 110, 30);
    
    txtDtIni.setVlrDate(new Date());
    txtDtFim.setVlrDate(new Date());

    tab.adicColuna("St");
    tab.adicColuna("Orc.");
    tab.adicColuna("Ped.");
    tab.adicColuna("NF");
    tab.adicColuna("C�d.Cli.");
    tab.adicColuna("Nome do cliente");

    tab.adicColuna("Data");
    tab.adicColuna("Validade");
    tab.adicColuna("Autoriz.");
    tab.adicColuna("Vlr.It.Orc.");
    tab.adicColuna("Cidade");
    tab.adicColuna("Fone");

    tab.setTamColuna(30, 0);
    tab.setTamColuna(40, 1);
    tab.setTamColuna(40, 2);
    tab.setTamColuna(40, 3);
    
    tab.setTamColuna(50, 4);
    tab.setTamColuna(180, 5);

    tab.setTamColuna(90, 6);
    tab.setTamColuna(90, 7);
    tab.setTamColuna(90, 8);
    tab.setTamColuna(90, 9);
    tab.setTamColuna(100, 10);
    tab.setTamColuna(100, 11);
    tab.setTamColuna(90, 12);

    btBusca.addActionListener(this);
    btPrevimp.addActionListener(this);

    tab.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent mevt) {
        if (mevt.getSource() == tab && mevt.getClickCount() == 2)
          abreOrc();
      }
    });
    btConsVenda.addActionListener(this);
    btSair.addActionListener(this);
  }
  /**
   * Carrega os valores para a tabela de consulta. Este m�todo � executado ap�s
   * carregar o ListaCampos da tabela.
   */
  private void carregaTabela() {
    String sWhere = "";
    boolean bUsaOr = false;
    boolean bUsaWhere = false;
    boolean bCli = (txtCodCli.getVlrInteger().intValue() > 0);

    if (cbAberto.getVlrString().equals("S")) {
      bUsaWhere = true;
      sWhere = " STATUSORC ='OA'";
    }
    if (cbCompleto.getVlrString().equals("S")) {
      if (sWhere.trim().equals("")) {
        sWhere = " STATUSORC ='OC'";
      }
      else {
        sWhere = sWhere + " OR STATUSORC ='OC'";
        bUsaOr = true;
      }
      bUsaWhere = true;
    }
    if (cbLiberado.getVlrString().equals("S")) {
      if (sWhere.trim().equals("")) {
        sWhere = " STATUSORC ='OL' AND IT.APROVITORC='S' AND IT.EMITITORC='N'";

      }
      else {
        sWhere = sWhere + " OR STATUSORC ='OL'";
        bUsaOr = true;
        bUsaWhere = true;
      }
      bUsaWhere = true;
    }
    if (cbFaturado.getVlrString().equals("S")) {
      if (sWhere.trim().equals(""))
        sWhere = " (STATUSORC ='OV' OR IT.EMITITORC='S')";
      else {
        sWhere = sWhere + " OR STATUSORC ='OV'";
        bUsaOr = true;
      }
      bUsaWhere = true;
    }

    if (bUsaWhere && bUsaOr)
      sWhere = " AND (" + sWhere + ")";
    else if (bUsaWhere)
      sWhere = " AND " + sWhere;
    else
      sWhere = " AND STATUSORC=''";

    if (gbVenc.getVlrString().equals("V"))
      sWhere += " AND DTVENCORC BETWEEN ? AND ?";
    else
      sWhere += " AND DTORC BETWEEN ? AND ?";

    if (bCli)
      sWhere += " AND O.CODCLI=? AND O.CODEMPCL=? AND CODFILIALCL=? ";

    if (!txtValorMenor.getVlrString().equals("")) {
      if (!txtValorMaior.getVlrString().equals("")) {
        sWhere += " AND IT.VLRLIQITORC >= " + txtValorMenor.getVlrBigDecimal();
        sWhere += " AND IT.VLRLIQITORC<= " + txtValorMaior.getVlrBigDecimal();
      }
    } 

    String sSQLFatura = "(SELECT VO.CODVENDA FROM VDVENDAORC VO WHERE VO.CODEMPOR=IT.CODEMP " +
						"AND VO.CODFILIALOR=IT.CODEMP AND VO.TIPOORC=IT.TIPOORC) ";

    String sSQL = "SELECT O.STATUSORC,O.CODORC,O.DTORC,O.DTVENCORC,"
        + "O.CODCLI,CL.NOMECLI,CL.FONECLI , IT.VENCAUTORIZORC,IT.NUMAUTORIZORC,"
        + "CL.CIDCLI,IT.APROVITORC,IT.VLRLIQITORC," +sSQLFatura 
        + "FROM  VDORCAMENTO O,VDCLIENTE CL,"
        + "VDITORCAMENTO IT " 
		+ "WHERE O.CODEMP=? "
        + "AND O.CODFILIAL=? AND O.TIPOORC='O' "
        + "AND IT.CODORC=O.CODORC AND IT.CODEMP=O.CODEMP AND IT.CODFILIAL=O.CODFILIAL "
        + "AND IT.TIPOORC=O.TIPOORC "
        + "AND CL.CODEMP=O.CODEMPCL AND CL.CODFILIAL=O.CODFILIALCL "
        + "AND CL.CODCLI=O.CODCLI" + sWhere;

    System.out.println(sSQL);
    try {
      PreparedStatement ps = con.prepareStatement(sSQL);
      ps.setInt(1, Aplicativo.iCodEmp);
      ps.setInt(2, ListaCampos.getMasterFilial("VDORCAMENTO"));
      ps.setDate(3, Funcoes.dateToSQLDate(txtDtIni.getVlrDate()));
      ps.setDate(4, Funcoes.dateToSQLDate(txtDtFim.getVlrDate()));

      if (bCli) {
        ps.setInt(5, txtCodCli.getVlrInteger().intValue());
        ps.setInt(6, Aplicativo.iCodEmp);
        ps.setInt(7, lcCli.getCodFilial());
      }

      ResultSet rs = ps.executeQuery();
      int iLin = 0;

      tab.limpa();
      while (rs.next()) {
        tab.adicLinha();
        tab.setValor(rs.getString(1) + "", iLin, 0);
        tab.setValor(new Integer(rs.getInt(2)), iLin, 1);

        tab.setValor(rs.getString(13)==null?"-":rs.getString(13)+"",iLin,2);
        tab.setValor("NF.",iLin,3);
        
        tab.setValor(rs.getInt(5) + "", iLin, 4);
        tab.setValor(rs.getString(6) != null ? rs.getString(6) : "", iLin, 5);
        tab.setValor(Funcoes.sqlDateToStrDate(rs.getDate(3)), iLin, 6);
        tab.setValor(Funcoes.sqlDateToStrDate(rs.getDate(4)), iLin, 7);
        tab.setValor(Funcoes.strDecimalToStrCurrency(2, rs.getString(12) != null ? rs.getString(12) : ""), iLin, 9);
        tab.setValor(rs.getString(9) != null ? rs.getString(9) : "", iLin, 8);
        tab.setValor(rs.getString(10) != null ? rs.getString(10) : "", iLin, 10);
        tab.setValor(rs.getString(7) != null ? rs.getString(7) : "", iLin, 11);

        iLin++;
      }
      //			rs.close();
      //			ps.close();
      if (!con.getAutoCommit())
        con.commit();
    }
    catch (SQLException err) {
      Funcoes.mensagemErro(this, "Erro ao carregar a tabela VDOR�AMENTO!\n"
          + err.getMessage());
      err.printStackTrace();
    }
  }

  private void imprimir(boolean bVisualizar) {
    ImprimeOS imp = new ImprimeOS("", con);
    int linPag = imp.verifLinPag() - 1;
    BigDecimal bTotalLiq = new BigDecimal("0");
    boolean bImpFat = false;
    
    bImpFat = Funcoes.mensagemConfirma(this,"Deseja imprimir informa��es de faturamento do or�amento?")==0?true:false;
    
    
    
    imp.montaCab();
    imp.setTitulo("Relat�rio de Or�amentos");

    try {
      imp.limpaPags();
      for (int iLin = 0; iLin < tab.getNumLinhas(); iLin++) {
        if (imp.pRow() == 0) {
          imp.impCab(136);
          //	imp.say(imp.pRow()+1,0,""+imp.comprimido());
          imp.say(imp.pRow() + 0, 1, "| N.ORC.");
          imp.say(imp.pRow() + 0, 15, "| Emiss�o");
          imp.say(imp.pRow() + 0, 29, "| Validade.");
          imp.say(imp.pRow() + 0, 41, "| Autoriz.");
          imp.say(imp.pRow() + 0, 56, "| Nome");
          imp.say(imp.pRow() + 0, 87, "| Valor do Item");
          imp.say(imp.pRow() + 0, 105, "| Cidade");
          imp.say(imp.pRow() + 0, 124, "| Telefone   ");
          imp.say(imp.pRow() + 0, 136, "|");
          imp.say(imp.pRow() + 1, 0, "" + imp.comprimido());

          imp.say(imp.pRow() + 0, 0, Funcoes.replicate("-", 136));

        }

        imp.say(imp.pRow() + 1, 0, "" + imp.comprimido());
        imp.say(imp.pRow() + 0, 2, "|"
            + Funcoes.alinhaDir(tab.getValor(iLin, 1) + "", 8));
        imp.say(imp.pRow() + 0, 15, "|"
            + Funcoes.alinhaDir(tab.getValor(iLin, 4) + "", 8));
        imp.say(imp.pRow() + 0, 29, "|"
            + Funcoes.alinhaDir(tab.getValor(iLin, 5) + "", 10));
        imp.say(imp.pRow() + 0, 41, "|"
            + Funcoes.alinhaDir(tab.getValor(iLin, 6) + "", 13));

        imp.say(imp.pRow() + 0, 56, "|"
            + Funcoes.copy(tab.getValor(iLin, 3) + "", 25));
        imp.say(imp.pRow() + 0, 87, "|"
            + Funcoes.alinhaDir(tab.getValor(iLin, 7) + "", 15));
        imp.say(imp.pRow() + 0, 105, "|"
            + Funcoes.copy(tab.getValor(iLin, 8) + "", 10));
        imp.say(imp.pRow() + 0, 124, "|"
            + Funcoes.alinhaDir(tab.getValor(iLin, 9) + "", 12));
        imp.say(imp.pRow() + 0, 136, "|");
        imp.say(imp.pRow() + 1, 0, "+ " + Funcoes.replicate("-", 133));
        imp.say(imp.pRow() + 0, 136, "+");

        if (tab.getValor(iLin, 7) != null) {
          bTotalLiq = bTotalLiq.add(new BigDecimal(Funcoes.strCurrencyToDouble(""
              + tab.getValor(iLin, 7))));
        }

        if (imp.pRow() >= linPag) {
          imp.incPags();
          imp.eject();
        }
      }

      imp.say(imp.pRow() + 1, 0, "" + imp.comprimido());
      imp.say(imp.pRow() + 0, 0, "|");
      imp.say(imp.pRow() + 0, 58, "   Valor total de itens:   "
          + Funcoes.strDecimalToStrCurrency(11, 2, "     " + bTotalLiq));
      imp.say(imp.pRow() + 0, 136, "|");

      imp.say(imp.pRow() + 1, 0, "" + imp.comprimido());
      imp.say(imp.pRow() + 0, 0, Funcoes.replicate("=", 136));
      imp.eject();

      imp.fechaGravacao();

      //      rs.close();
      //      ps.close();
      if (!con.getAutoCommit())
        con.commit();

    }
    catch (SQLException err) {
      Funcoes.mensagemErro(this, "Erro consulta tabela de or�amentos!"
          + err.getMessage());
    }

    if (bVisualizar) {
      imp.preview(this);
    }
    else {
      imp.print();
    }
  }

  private void abreOrc() {
    int iCodOrc = ((Integer) tab.getValor(tab.getLinhaSel(), 1)).intValue();
    if (fPrim.temTela("Orcamento") == false) {
      FOrcamento tela = new FOrcamento();
      fPrim.criatela("Orcamento", tela, con);
      tela.exec(iCodOrc);
    }
  }
  private void consVenda() {
    int iCodVenda = 0;
    int iLin = tab.getLinhaSel();
    if (iLin >= 0) {
      if (!tab.getValor(iLin, 0).toString().equals("OV"))
        return;
      try {
        String sSQL = "SELECT CODVENDA FROM VDVENDAORC WHERE CODORC=?"+
                      " AND CODEMPOR=? AND CODFILIALOR=?";
        PreparedStatement ps = con.prepareStatement(sSQL);
        ps.setInt(1, Integer.parseInt(tab.getValor(iLin, 1).toString()));
        ps.setInt(2, Aplicativo.iCodEmp);
        ps.setInt(3, ListaCampos.getMasterFilial("VDORCAMENTO"));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          iCodVenda = rs.getInt("CodVenda");
        }
        rs.close();
        ps.close();
        if (!con.getAutoCommit())
          con.commit();
      }
      catch (SQLException err) {
        Funcoes.mensagemErro(this, "Erro ao buscar a venda!\n"+err.getMessage());
        err.printStackTrace();
        return;
      }
      DLConsultaVenda dl = new DLConsultaVenda(this, con, iCodVenda);
      dl.setVisible(true);
      dl.dispose();
    }
  }
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btSair) {
      dispose();
    }
    if (evt.getSource() == btConsVenda) {
      consVenda();
    }
    if (evt.getSource() == btBusca) {
      if (txtDtIni.getVlrString().length() < 10)
        Funcoes.mensagemInforma(this, "Digite a data inicial!");
      else if (txtDtFim.getVlrString().length() < 10)
        Funcoes.mensagemInforma(this, "Digite a data final!");
      else
        carregaTabela();
      if (evt.getSource() == btPrevimp) {
        imprimir(true);
      }
    }
    if (evt.getSource() == btPrevimp) {
      imprimir(true);
    }

  }

  public void setTelaPrim(FPrincipal fP) {
    fPrim = fP;
  }
  public void setConexao(Connection cn) {
    con = cn;
    lcCli.setConexao(con);
  }
}