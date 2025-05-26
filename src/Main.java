import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;



class Consulta {
    String CPF;
    String especialidade;
    String medico;
    String recepcionista;
    String dataConsulta;
    String horaConsulta;

    public Consulta(String CPF, String especialidade, String medico, String recepcionista, String dataConsulta, String horaConsulta) {
        this.CPF = CPF;
        this.especialidade = especialidade;
        this.medico = medico;
        this.recepcionista = recepcionista;
        this.dataConsulta = dataConsulta;
        this.horaConsulta = horaConsulta;
    }

    public String getCPF() {
        return CPF;
    }
    public String getEspecialidade() {
        return especialidade;
    }
    public String getMedico() {
        return medico;
    }
    public String getRecepcionista() {
        return recepcionista;
    }
    public String getDataConsulta() {
        return dataConsulta;
    }
    public String getHoraConsulta() {
        return horaConsulta;
    }
}

abstract class Pessoa {
    protected String CPF;
    protected String nome;
    protected int idade;

    public Pessoa(String CPF, String nome, int idade) {
        this.CPF = CPF;
        this.nome = nome;
        this.idade = idade;
    }

    public String getCPF() {
        return this.CPF;
    }

    public String getNome() {
        return this.nome;
    }

    public int getIdade() {
        return idade;
    }

    public abstract String exibirDetalhes();
}

class Paciente extends Pessoa {
    private String plano;
    private String recepcionistaCadastro;

    public Paciente(String CPF, String nome, int idade, String plano, String recepcionistaCadastro) {
        super(CPF, nome, idade);
        this.plano = plano;
        this.recepcionistaCadastro = recepcionistaCadastro;
    }

    public String getPlano() {
        return plano;
    }

    public String getRecepcionistaCadastro() {
        return recepcionistaCadastro;
    }

    @Override
    public String exibirDetalhes() {
        return "\nCPF: " + CPF + "\nNome: " + nome + "\nIdade: " + idade + "\nPlano: " + plano + "\nRecepcionista: " + recepcionistaCadastro;
    }
}

class Recepcionista extends Pessoa {
    private String turno;

    public Recepcionista(String CPF, String nome, int idade, String turno) {
        super(CPF, nome, idade);
        this.turno = turno;
    }

    public String getTurno() {
        return turno;
    }

    @Override
    public String exibirDetalhes() {
        return "CPF: " + CPF + "\nNome: " + nome + "\nIdade: " + idade + "\nTurno: " + turno + "\n\n";
    }
}

class Medico extends Pessoa {
    private String especialidade;
    private String crm;

    public Medico(String CPF, String nome, int idade, String especialidade, String crm) {
        super(CPF, nome, idade);
        this.especialidade = especialidade;
        this.crm = crm;
    }

    @Override
    public String exibirDetalhes() {
        return "\nCPF: " + CPF + "\nNome: " + nome + "\nIdade: " + idade + "\nEspecialidade: " + especialidade + "\nCRM:" + crm + "\n\n";
    }
}

class Hospital extends JFrame {
    private ArrayList<Paciente> pacientes = new ArrayList<>();
    private ArrayList<Consulta> consultas = new ArrayList<>();
    private List<String> planosDeSaude;
    private List<Recepcionista> recepcionistas;
    private HashMap<String, List<Medico>> especialidadeMedicos = new HashMap<>();
    private JLabel relogioLabel;

    private void criarArquivosSeNaoExistirem() throws IOException {

        File arquivoPacientes = new File("cadastros_pacientes.txt");
        File arquivoConsultas = new File("consultas_marcadas.txt");
        if (arquivoConsultas.exists()) {
            return;
        }
        if(arquivoPacientes.exists()) {
            return;
        }
        arquivoConsultas.createNewFile();
        arquivoPacientes.createNewFile();
    }

    public Hospital() throws IOException {
        setTitle("Marcar Consultas - Hospital");
        setSize(1000, 500);
        criarArquivosSeNaoExistirem();
        planosDeSaude = List.of("Bronze", "Prata", "Ouro");
        recepcionistas = List.of(
                new Recepcionista("00000000000", "Recepcionista A", 28, "Dia"),
                new Recepcionista("11111111111", "Recepcionista B", 28, "Tarde"),
                new Recepcionista("22222222222", "Recepcionista C", 30, "Noite")
        );

        especialidadeMedicos.put("Cardiologia", List.of(
                new Medico("33333333333", "Dr. Silva", 45, "Cardiologia", "12121212121"),
                new Medico("44444444444", "Dra. Koultrapali", 50, "Cardiologia", "21212121212")
        ));

        especialidadeMedicos.put("Neurologia", List.of(
                new Medico("55555555555", "Dr. Yamada", 45, "Neurologia", "34343434343"),
                new Medico("66666666666", "Dr. Bloodmoon", 50, "Neurologia", "43434343434")
        ));

        especialidadeMedicos.put("Urologia", List.of(
                new Medico("77777777777", "Dra. Maier", 45, "Urologia", "56565656565"),
                new Medico("88888888888", "Dr. Neuer", 50, "Urologia", "65656565656")
        ));

        especialidadeMedicos.put("Dermatologista", List.of(
                new Medico("99999999999", "Dr. Brumado", 45, "Dermatologista","78787878787"),
                new Medico("12345678910", "Dra. Souza", 50, "Dermatologista", "87878787878")
        ));

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        JPanel painelCentral = new JPanel(new GridLayout(6, 1, 20, 20));

        JButton botaoCadastrarPaciente = new JButton("Cadastrar Paciente");
        botaoCadastrarPaciente.addActionListener(e -> mostrarFormularioCadastro());

        JButton botaoMarcarConsulta = new JButton("Marcar Consulta");
        botaoMarcarConsulta.addActionListener(e -> mostrarFormularioConsulta());

        JButton botaoVisualizarConsultas = new JButton("Visualizar Consultas");
        botaoVisualizarConsultas.addActionListener(e -> {
            JTextArea consultasArea = new JTextArea();
            JScrollPane consultasScrollPane = new JScrollPane(consultasArea);
            String consultasTexto = carregarConsultas();
            consultasArea.setText(consultasTexto);
            JFrame detalhesFrame = new JFrame("Detalhes das Consultas");
            detalhesFrame.add(consultasScrollPane);
            detalhesFrame.setSize(600, 400);
            detalhesFrame.setVisible(true);
        });

        JButton botaoDetalhesMedicos = new JButton("Detalhes Médicos");
        ArrayList<Medico> medicos = new ArrayList<>();
        for (List<Medico> medico : especialidadeMedicos.values()) {
            medicos.addAll(medico);
        }
        botaoDetalhesMedicos.addActionListener(e -> {
            JTextArea medicosArea = new JTextArea(20, 50);
            medicosArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(medicosArea);
            StringBuilder detalhes = new StringBuilder();
            for (Medico medico : medicos) {
                detalhes.append(medico.exibirDetalhes());
            }
            medicosArea.setText(detalhes.toString());
            JFrame detalhesFrame = new JFrame("Detalhes dos Médicos");
            detalhesFrame.add(scrollPane);
            detalhesFrame.setSize(600, 400);
            detalhesFrame.setVisible(true);
        });

        JButton botaoDetalhesRecepcionistas = new JButton("Detalhes Recepcionistas");
        botaoDetalhesRecepcionistas.addActionListener(e -> {
            JTextArea textArea = new JTextArea(20, 50);  // 20 linhas e 50 colunas (ajustáveis)
            textArea.setEditable(false);  // Não permite edição
            JScrollPane scrollPane = new JScrollPane(textArea);
            StringBuilder detalhes = new StringBuilder();
            for (Recepcionista recepcionista : recepcionistas) {
                detalhes.append(recepcionista.exibirDetalhes());
            }
            textArea.setText(detalhes.toString());
            JFrame detalhesFrame = new JFrame("Detalhes dos Recepcionistas");
            detalhesFrame.add(scrollPane);
            detalhesFrame.setSize(600, 400);
            detalhesFrame.setVisible(true);
        });

        JButton botaoDetalhesPacientes = new JButton("Detalhes Pacientes");
        botaoDetalhesPacientes.addActionListener(e -> {
            try {
                carregarPacientes();
            } catch (RuntimeException x) {
                JOptionPane.showMessageDialog(null, "Nenhum Paciente Cadastrado", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JTextArea textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            StringBuilder detalhes = new StringBuilder();
            for (Paciente paciente : pacientes) {
                detalhes.append(paciente.exibirDetalhes());
            }
            textArea.setText(detalhes.toString());
            JFrame detalhesFrame = new JFrame("Detalhes dos Pacientes");
            detalhesFrame.add(scrollPane);
            detalhesFrame.setSize(600, 400);
            detalhesFrame.setVisible(true);
        });

        painelCentral.add(botaoCadastrarPaciente);
        painelCentral.add(botaoMarcarConsulta);
        painelCentral.add(botaoVisualizarConsultas);
        painelCentral.add(botaoDetalhesMedicos);
        painelCentral.add(botaoDetalhesRecepcionistas);
        painelCentral.add(botaoDetalhesPacientes);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);


        relogioLabel = new JLabel();
        relogioLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        painelPrincipal.add(relogioLabel, BorderLayout.NORTH);
        add(painelPrincipal);
        atualizarRelogio();


    }

    public void carregarPacientes(){
        pacientes.clear();
        File arquivo = new File("cadastros_pacientes.txt");
        try {BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 5) continue;
                String CPF = dados[0];
                String nome = dados[1];
                int idade = Integer.parseInt(dados[2]);
                String plano = dados[3];
                String recepcionistaCadastro = dados[4];
                pacientes.add(new Paciente(CPF, nome, idade, plano, recepcionistaCadastro));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String carregarConsultas() {
        consultas.clear();
        StringBuilder sb = new StringBuilder();
        File arquivo = new File("consultas_marcadas.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 6) continue;
                String CPF = dados[0];
                String especialidade = dados[1];
                String medico = dados[2];
                String recepcionista = dados[3];
                String dataConsulta = dados[4];
                String horaConsulta = dados[5];
                consultas.add(new Consulta(CPF, especialidade, medico, recepcionista, dataConsulta, horaConsulta));
                sb.append("CPF: ").append(CPF)
                        .append(", Especialidade: ").append(especialidade)
                        .append(", Médico: ").append(medico)
                        .append(", Recepcionista: ").append(recepcionista)
                        .append(", Data: ").append(dataConsulta)
                        .append(", Hora: ").append(horaConsulta)
                        .append("\n\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private void atualizarRelogio() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                String horaAtual = formatoHora.format(new Date());
                relogioLabel.setText("Hora Atual: " + horaAtual);
            }
        }, 0, 1000);
    }

    private void mostrarFormularioCadastro() {
        JPanel painelFormulario = new JPanel(new GridLayout(7, 1, 20, 20));

        JLabel labelNome = new JLabel("Nome: ");
        JTextField fieldNome = new JTextField();

        JLabel labelCPF = new JLabel("CPF: ");
        JTextField fieldCPF = new JTextField();

        JLabel labelIdade = new JLabel("Idade: ");
        JTextField fieldIdade = new JTextField();

        JLabel labelPlano = new JLabel("Plano de Saúde: ");
        JComboBox<String> comboPlano = new JComboBox<>(planosDeSaude.toArray(new String[0]));

        JLabel labelRecepcionista = new JLabel("Recepcionista: ");
        ArrayList<String> rnome = new ArrayList<>();
        for (Recepcionista recepcionista : recepcionistas) {
            rnome.add(recepcionista.getNome());}
        JComboBox<String> comboRecepcionista = new JComboBox<>(rnome.toArray(new String[0]));

        JButton botaoCadastrarPaciente = new JButton("Cadastrar Paciente");
        botaoCadastrarPaciente.addActionListener(e -> {
            int idade;
            try {
                idade = Integer.parseInt(fieldIdade.getText());
                String nome = fieldNome.getText();
                String cpf = fieldCPF.getText();
                String plano = (String) comboPlano.getSelectedItem();
                String recepcionista = (String) comboRecepcionista.getSelectedItem();
                if (!nome.isEmpty() && !cpf.isEmpty() && plano != null && recepcionista != null) {
                    Paciente paciente = new Paciente(cpf, nome, idade, plano, recepcionista);
                    cadastrarPaciente(paciente);
                } else {JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);}

            } catch (NumberFormatException erro) {
                JOptionPane.showMessageDialog(null, "Idade inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        });

        painelFormulario.add(labelNome);
        painelFormulario.add(fieldNome);
        painelFormulario.add(labelCPF);
        painelFormulario.add(fieldCPF);
        painelFormulario.add(labelIdade);
        painelFormulario.add(fieldIdade);
        painelFormulario.add(labelPlano);
        painelFormulario.add(comboPlano);
        painelFormulario.add(labelRecepcionista);
        painelFormulario.add(comboRecepcionista);
        painelFormulario.add(botaoCadastrarPaciente);

        JOptionPane.showMessageDialog(this, painelFormulario, "Cadastrar Paciente", JOptionPane.PLAIN_MESSAGE);
    }


    private void mostrarFormularioConsulta() {
        carregarConsultas();
        JPanel painelConsulta = new JPanel(new GridLayout(7, 1, 20, 20));

        JLabel labelCPF = new JLabel("CPF: ");
        JTextField fieldCPF = new JTextField();

        JLabel labelEspecialidade = new JLabel("Especialidade: ");
        JComboBox<String> comboEspecialidade = new JComboBox<>(especialidadeMedicos.keySet().toArray(new String[0]));

        JLabel labelMedico = new JLabel("Médico: ");
        JComboBox<String> comboMedico = new JComboBox<>();

        comboEspecialidade.addActionListener(e -> {
            String especialidadeSelecionada = (String) comboEspecialidade.getSelectedItem();
            if (comboEspecialidade.getSelectedItem() != null) {
                comboMedico.removeAllItems();
            }
            for (Medico medico : especialidadeMedicos.get(especialidadeSelecionada)) {
                comboMedico.addItem(medico.getNome());
            }
        });

        JLabel labelRecepcionista = new JLabel("Recepcionista: ");
        ArrayList<String> rnome = new ArrayList<>();
        for (Recepcionista recepcionista : recepcionistas) {
            rnome.add(recepcionista.getNome());}
        JComboBox<String> comboRecepcionista = new JComboBox<>(rnome.toArray(new String[0]));


        // FEITO POR GPT
        JLabel labelData = new JLabel("Data da Consulta:");
        JSpinner spinnerData = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerData, "dd/MM/yyyy");
        spinnerData.setEditor(dateEditor);
        JLabel labelHora = new JLabel("Hora da Consulta:");
        JSpinner spinnerHora = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinnerHora, "HH:mm");
        spinnerHora.setEditor(timeEditor);
        //

        JButton botaoAgendar = new JButton("Agendar Consulta");
        botaoAgendar.addActionListener(e -> {
            String cpf = fieldCPF.getText().trim();
            String especialidade = (String) comboEspecialidade.getSelectedItem();
            String medico = (String) comboMedico.getSelectedItem();
            String recepcionista = (String) comboRecepcionista.getSelectedItem();


            if (cpf.isEmpty() || especialidade == null || especialidade.isEmpty() || medico == null || medico.isEmpty() || recepcionista == null || recepcionista.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha Todos os Campos", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //GPT
            Date data = (Date) spinnerData.getValue();
            Date hora = (Date) spinnerHora.getValue();
            Calendar dataConsultaCalendar = Calendar.getInstance();
            dataConsultaCalendar.setTime(data);
            Calendar horaConsultaCalendar = Calendar.getInstance();
            horaConsultaCalendar.setTime(hora);
            dataConsultaCalendar.set(Calendar.HOUR_OF_DAY, horaConsultaCalendar.get(Calendar.HOUR_OF_DAY));
            dataConsultaCalendar.set(Calendar.MINUTE, horaConsultaCalendar.get(Calendar.MINUTE));
            dataConsultaCalendar.set(Calendar.SECOND, 0);
            Calendar agora = Calendar.getInstance();
            if (dataConsultaCalendar.before(agora)) {
                JOptionPane.showMessageDialog(null, "Data/Hora Inválidas", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //

            if (!pacienteExiste(cpf)) {
                JOptionPane.showMessageDialog(null, "Paciente não Existe", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //GPT
            SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm");
            String dataConsulta = dataFormat.format(data);
            String horaConsulta = horaFormat.format(hora);
            //
            Consulta consulta = new Consulta(cpf, especialidade, medico, recepcionista, dataConsulta, horaConsulta);
            salvarConsultas(consulta);
            JOptionPane.showMessageDialog(null, "Consulta marcada com sucesso!", "Confirmação", JOptionPane.INFORMATION_MESSAGE);
        });

        painelConsulta.add(labelCPF);
        painelConsulta.add(fieldCPF);
        painelConsulta.add(labelEspecialidade);
        painelConsulta.add(comboEspecialidade);
        painelConsulta.add(labelMedico);
        painelConsulta.add(comboMedico);
        painelConsulta.add(labelRecepcionista);
        painelConsulta.add(comboRecepcionista);
        painelConsulta.add(labelData);
        painelConsulta.add(spinnerData);
        painelConsulta.add(labelHora);
        painelConsulta.add(spinnerHora);
        painelConsulta.add(botaoAgendar);

        JOptionPane.showMessageDialog(this, painelConsulta, "Marcar Consulta", JOptionPane.PLAIN_MESSAGE);
    }

    private boolean pacienteExiste(String cpf) {
        try {
            carregarPacientes();
        }
        catch (RuntimeException erro) {
            return false;
        }
            for (Paciente paciente : pacientes) {
                if (paciente.getCPF().equals(cpf)) {
                    return true;
            }
        }
        return false;
    }

    private void cadastrarPaciente(Paciente paciente) {
        for (Paciente p : pacientes) {
            if (p.getCPF().equals(paciente.getCPF())) {
                JOptionPane.showMessageDialog(null, "Paciente já Cadastrado com Esse CPF", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        salvarCadastroPacientes(paciente);
        JOptionPane.showMessageDialog(null, "Paciente Cadastrado com Sucesso", null, JOptionPane.INFORMATION_MESSAGE);
    }

    public void salvarCadastroPacientes(Paciente paciente) {
        File arquivo = new File("cadastros_pacientes.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(paciente.getCPF() + ";" +
                    paciente.getNome() + ";" +
                    paciente.getIdade() + ";" +
                    paciente.getPlano() + ";" +
                    paciente.getRecepcionistaCadastro() + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void salvarConsultas(Consulta consulta) {
        File arquivo = new File("consultas_marcadas.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(consulta.getCPF() + ";" +
                    consulta.getEspecialidade() + ";" +
                    consulta.getMedico() + ";" +
                    consulta.getRecepcionista() + ";" +
                    consulta.getDataConsulta() + ";" +
                    consulta.getHoraConsulta() + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar consulta", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Hospital hospital;
            try {
                hospital = new Hospital();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hospital.setVisible(true);
        });
    }
}