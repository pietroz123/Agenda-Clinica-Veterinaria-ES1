package agenda;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bianca
 */
public class FacadeAgenda {
    
    // Variáveis de Instância
    Cliente cliente = null;
    Paciente paciente = null;
    Veterinario veterinario = null;
    Agendamento agendamento = null;
    
    private Connection conn;
    
    // Setters
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }
    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }
    public static void main(String[] args){
        new FrameOpcoesAgenda().setVisible(true);
    }
    
    public boolean agendar() {
        
        // Verifica se o agendamento está no expediente da Clínica
        if (!agendamento.verificarExpediente(agendamento.getHorarioAgendamento())) {
            JOptionPane.showMessageDialog(null, "Horário fora do Expediente!");
            return false;
        }

        
        // Recupera a conexão
        conn = Conexao.getConnection();
        OperacoesBD op = new OperacoesBD();              
        
        
        // Verifica a disponibilidade do Veterinario
        try {
            if (!op.verificarDisponibilidade(conn, this.agendamento.getDataAgendamento().toString(), this.agendamento.getHorarioAgendamento().toString(), this.veterinario.getCRMV())) {
                JOptionPane.showMessageDialog(null, "Horário Indisponivel nos Agendamentos do Veterinario!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacadeAgenda.class.getName()).log(Level.SEVERE, null, ex);
        }

        ResultSet rsV = null;
        ResultSet rsC = null;
        ResultSet rsP = null;
        Cliente c = new Cliente();
        Paciente p = new Paciente();
        Veterinario v = new Veterinario();


        // Busca se existe o Veterinário
        try {
            rsV = op.buscarVeterinario(conn, this.veterinario.getCRMV());
            if (!rsV.next()) {
                JOptionPane.showMessageDialog(null, "Veterinário não Cadastrado!\nFavor inserir outro.");    
                return false;
            }
            else {
                rsV.first();
                v.setNomeVeterinario(rsV.getString("nome_veterinario"));
                v.setCRMV(rsV.getString("CRMV_veterinario"));
                v.setCelularVeterinario(rsV.getString("celular_veterinario"));
                v.setEmailVeterinario(rsV.getString("email_veterinario"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Veterinário não Cadastrado!\nFavor inserir outro.");
        }
        
        //Busca se existe o Paciente
        try {
            rsP = op.buscarPaciente(conn, this.paciente.getNomePaciente());
            if (!rsP.next()) {
                JOptionPane.showMessageDialog(null, "Paciente não Cadastrado!\nFavor Realizar o Cadastro.");    
                return false;
            }
            else {
                rsP.first();
                p.setNomePaciente(rsP.getString("nome_paciente"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Paciente não Cadastrado!\nFavor Realizar o Cadastro.");
        }
        
        //Busca se existe o Cliente
        try {
            rsC = op.buscarCliente(conn, this.cliente.getCPFCliente());
            if (!rsC.next()) {
                JOptionPane.showMessageDialog(null, "Cliente não Cadastrado!\nFavor Realizar o Cadastro.");    
                return false;
            }
            else {
                rsC.first();
                c.setNomeCliente(rsC.getString("nome_cliente"));
                c.setCPFCliente(rsC.getString("cpf_cliente"));
                c.setCelularCliente(rsC.getString("celular_cliente"));
                c.setEmailCliente(rsC.getString("email_cliente"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,  "Cliente não Cadastrado!\nFavor Realizar o Cadastro.");
        }
        
        new FrameConfirmarAgendamento(c, p, v, this.agendamento).setVisible(true);
        return true;
    
    }

    public boolean remover() {

        OperacoesBD op = new OperacoesBD();
        try {
            conn = Conexao.getConnection();
            int resultado = op.removerAgendamento(conn, this.agendamento, this.cliente.getCPFCliente());
            
            if (resultado != 0) {
                JOptionPane.showMessageDialog(null, "Agendamento Excluído com Sucesso!"); 
                return true;           
            } else {
                JOptionPane.showMessageDialog(null, "Falha na Exclusão do Agendamento!");
                return false;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Falha na Exclusão do Agendamento!");
            return false;
        }

    }

    public boolean alterar(Agendamento antigo) {

        OperacoesBD op = new OperacoesBD();
        try {
            conn = Conexao.getConnection();

            op.atualizarAgendamento(conn, antigo, this.agendamento, this.cliente, this.veterinario, this.paciente);
            
            JOptionPane.showMessageDialog(null, "Agendamento Alterado com Sucesso!");
            return true;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Falha na Alteração do Agendamento!\n" + ex);
            return false;
        }

    }
            
            
}
