import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * ScriptMint – Freelance Invoice Generator
 * Full Swing + JDBC + MySQL desktop application.
 *
 * HOW TO RUN:
 *   1. Create the MySQL database (see scriptmint_db.sql).
 *   2. Update DBConnection.java with your MySQL password.
 *   3. Add mysql-connector-j-x.x.x.jar to classpath.
 *   4. Compile:  javac -cp .;mysql-connector-j-8.x.x.jar *.java
 *   5. Run:      java  -cp .;mysql-connector-j-8.x.x.jar ScriptMintApp
 *   (Use : instead of ; on Mac/Linux)
 */
public class ScriptMintApp extends JFrame {

    /* ─── Palette ───────────────────────────────────────────────────── */
    static final Color BG       = new Color(13, 17, 28);
    static final Color CARD     = new Color(22, 27, 44);
    static final Color PANEL    = new Color(30, 36, 56);
    static final Color ACCENT   = new Color(94, 162, 255);
    static final Color GREEN    = new Color(52, 211, 153);
    static final Color RED      = new Color(248, 81, 81);
    static final Color YELLOW   = new Color(251, 191, 36);
    static final Color TXT      = new Color(220, 228, 240);
    static final Color MUTED    = new Color(100, 116, 139);
    static final Color BORDER   = new Color(40, 50, 72);

    /* ─── DAOs ──────────────────────────────────────────────────────── */
    private final ClientDAO  clientDAO  = new ClientDAO();
    private final WorkLogDAO workLogDAO = new WorkLogDAO();

    /* ─── Layout ────────────────────────────────────────────────────── */
    private CardLayout cards;
    private JPanel     deck;

    /* ─── Dashboard labels ──────────────────────────────────────────── */
    private JLabel lbClients, lbLogs, lbRevenue;

    /* ─── Clients table ─────────────────────────────────────────────── */
    private DefaultTableModel clientModel;

    /* ════════════════════════════════════════════════════════════════ */
    public static void main(String[] args) {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new ScriptMintApp().setVisible(true);
        });
    }

    /* ════════════════════════════════════════════════════════════════ */
    ScriptMintApp() {
        setTitle("ScriptMint  —  Freelance Invoice Generator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 700);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
        add(sidebar(), BorderLayout.WEST);
        add(mainDeck(), BorderLayout.CENTER);
    }

    /* ═══════════════════════════  SIDEBAR  ═════════════════════════ */
    private JPanel sidebar() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(215, 0));
        p.setBackground(CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        // Logo
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        logo.setBackground(CARD);
        logo.setMaximumSize(new Dimension(215, 68));
        logo.setPreferredSize(new Dimension(215, 68));
        JLabel lbl = new JLabel("⚡ ScriptMint");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lbl.setForeground(ACCENT);
        logo.add(lbl);
        p.add(logo);

        p.add(hLine());

        p.add(navBtn("🏠  Dashboard",        "DASH"));
        p.add(navBtn("👤  Clients",           "CLIENTS"));
        p.add(navBtn("⏱   Log Work Hours",    "LOG"));
        p.add(navBtn("📄  Generate Invoice",  "INVOICE"));
        p.add(navBtn("✏   Update Client",     "UPDATE"));
        p.add(navBtn("🗑   Delete Records",    "DELETE"));

        p.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("ScriptMint v2.0  |  JDBC + MySQL");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ver.setForeground(MUTED);
        ver.setAlignmentX(CENTER_ALIGNMENT);
        p.add(ver);
        p.add(Box.createVerticalStrut(12));
        return p;
    }

    private JButton navBtn(String text, String card) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setForeground(TXT);
        b.setBackground(CARD);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(13, 18, 13, 18));
        b.setMaximumSize(new Dimension(215, 50));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(PANEL); }
            public void mouseExited (MouseEvent e) { b.setBackground(CARD);  }
        });
        b.addActionListener(e -> {
            cards.show(deck, card);
            if ("DASH".equals(card))    refreshDash();
            if ("CLIENTS".equals(card)) refreshClientsTable();
        });
        return b;
    }

    /* ═══════════════════════════  CARD DECK  ════════════════════════ */
    private JPanel mainDeck() {
        cards = new CardLayout();
        deck  = new JPanel(cards);
        deck.setBackground(BG);
        deck.add(buildDash(),    "DASH");
        deck.add(buildClients(), "CLIENTS");
        deck.add(buildLog(),     "LOG");
        deck.add(buildInvoice(), "INVOICE");
        deck.add(buildUpdate(),  "UPDATE");
        deck.add(buildDelete(),  "DELETE");
        return deck;
    }

    /* ══════════════════════════  DASHBOARD  ════════════════════════ */
    private JPanel buildDash() {
        JPanel p = base();
        p.setLayout(new BorderLayout(0, 24));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));

        p.add(pageTitle("Dashboard"), BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);
        lbClients = bigNum("0");
        lbLogs    = bigNum("0");
        lbRevenue = bigNum("₹0");
        stats.add(statCard("Total Clients",   lbClients, ACCENT));
        stats.add(statCard("Unbilled Logs",   lbLogs,    YELLOW));
        stats.add(statCard("Pending Revenue", lbRevenue, GREEN));
        p.add(stats, BorderLayout.CENTER);

        JLabel tip = new JLabel("  💡 Use the sidebar to navigate. Generate invoices marks logs as billed.");
        tip.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tip.setForeground(MUTED);
        p.add(tip, BorderLayout.SOUTH);

        refreshDash();
        return p;
    }

    private void refreshDash() {
        List<ClientDTO> cls = clientDAO.getAll();
        lbClients.setText(String.valueOf(cls.size()));
        int logs = 0, rev = 0;
        for (ClientDTO c : cls) {
            List<WorkLogDTO> wl = workLogDAO.getByClient(c.getId(), true);
            logs += wl.size();
            for (WorkLogDTO w : wl) rev += w.getHours() * c.getHourlyRate();
        }
        lbLogs.setText(String.valueOf(logs));
        lbRevenue.setText("₹" + rev);
    }

    private JPanel statCard(String label, JLabel num, Color accent) {
        JPanel c = new JPanel(new BorderLayout(0, 6));
        c.setBackground(CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accent, 1, true),
                new EmptyBorder(22, 26, 22, 26)));
        num.setFont(new Font("Segoe UI", Font.BOLD, 38));
        num.setForeground(accent);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(MUTED);
        c.add(num, BorderLayout.CENTER);
        c.add(lbl, BorderLayout.SOUTH);
        return c;
    }

    /* ══════════════════════════  CLIENTS  ══════════════════════════ */
    private JPanel buildClients() {
        JPanel p = base();
        p.setLayout(new BorderLayout(0, 16));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));
        p.add(pageTitle("Client List"), BorderLayout.NORTH);

        String[] cols = {"ID", "Client Name", "Hourly Rate (₹)"};
        clientModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = styledTable(clientModel);
        JScrollPane sp = new JScrollPane(tbl);
        styleScroll(sp);
        p.add(sp, BorderLayout.CENTER);

        // Add client form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        form.setOpaque(false);
        JTextField fName = field("Client Name");
        JTextField fRate = field("Hourly Rate");
        JButton btnAdd   = accentBtn("+ Add Client", ACCENT);
        form.add(fName); form.add(fRate); form.add(btnAdd);
        p.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            String name = fName.getText().trim();
            String rateStr = fRate.getText().trim();
            if (name.isEmpty() || rateStr.isEmpty()) {
                err("Please fill in both fields."); return;
            }
            try {
                int rate = Integer.parseInt(rateStr);
                if (rate <= 0) { err("Hourly rate must be positive."); return; }
                ClientDTO c = new ClientDTO(0, name, rate);
                if (clientDAO.add(c)) {
                    ok("Client '" + name + "' registered successfully!");
                    fName.setText(""); fRate.setText("");
                    refreshClientsTable();
                } else err("Failed to add client. Check DB connection.");
            } catch (NumberFormatException ex) { err("Hourly rate must be a number."); }
        });

        refreshClientsTable();
        return p;
    }

    private void refreshClientsTable() {
        if (clientModel == null) return;
        clientModel.setRowCount(0);
        for (ClientDTO c : clientDAO.getAll())
            clientModel.addRow(new Object[]{c.getId(), c.getName(), "₹" + c.getHourlyRate()});
    }

    /* ══════════════════════════  LOG WORK  ═════════════════════════ */
    private JPanel buildLog() {
        JPanel p = base();
        p.setLayout(new BorderLayout(0, 20));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));
        p.add(pageTitle("Log Work Hours"), BorderLayout.NORTH);

        JPanel form = formCard();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        JComboBox<ClientDTO> cbClient = clientCombo();
        JTextField fDesc  = field("e.g. Website redesign");
        JTextField fHours = field("e.g. 4");
        JButton    btnLog = accentBtn("⏱  Log Work", GREEN);
        JLabel     status = statusLbl();

        g.gridx=0; g.gridy=0; form.add(fLabel("Select Client:"), g);
        g.gridx=1;             form.add(cbClient, g);
        g.gridx=0; g.gridy=1; form.add(fLabel("Work Description:"), g);
        g.gridx=1;             form.add(fDesc, g);
        g.gridx=0; g.gridy=2; form.add(fLabel("Hours Worked:"), g);
        g.gridx=1;             form.add(fHours, g);
        g.gridx=0; g.gridy=3; g.gridwidth=2; form.add(btnLog, g);
        g.gridy=4;             form.add(status, g);

        // Refresh client combo when panel shown
        p.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) { refreshCombo(cbClient); }
        });

        btnLog.addActionListener(e -> {
            if (cbClient.getSelectedItem() == null) { status.setText("❌ No clients found. Add a client first."); status.setForeground(RED); return; }
            ClientDTO client = (ClientDTO) cbClient.getSelectedItem();
            String desc  = fDesc.getText().trim();
            String hrsStr = fHours.getText().trim();
            if (desc.isEmpty() || hrsStr.isEmpty()) { status.setText("❌ Fill in all fields."); status.setForeground(RED); return; }
            try {
                int hrs = Integer.parseInt(hrsStr);
                if (hrs <= 0) { status.setText("❌ Hours must be greater than 0."); status.setForeground(RED); return; }
                WorkLogDTO w = new WorkLogDTO(0, client.getId(), desc, hrs, false);
                if (workLogDAO.add(w)) {
                    status.setText("✅ Work logged: " + hrs + " hrs for " + client.getName());
                    status.setForeground(GREEN);
                    fDesc.setText(""); fHours.setText("");
                    refreshDash();
                } else { status.setText("❌ Failed to log work."); status.setForeground(RED); }
            } catch (NumberFormatException ex) { status.setText("❌ Hours must be a number."); status.setForeground(RED); }
        });

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    /* ══════════════════════════  INVOICE  ══════════════════════════ */
    private JTextArea invoiceArea;

    private JPanel buildInvoice() {
        JPanel p = base();
        p.setLayout(new BorderLayout(0, 16));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));
        p.add(pageTitle("Generate Invoice"), BorderLayout.NORTH);

        invoiceArea = new JTextArea();
        invoiceArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        invoiceArea.setBackground(new Color(10, 14, 22));
        invoiceArea.setForeground(GREEN);
        invoiceArea.setEditable(false);
        invoiceArea.setBorder(new EmptyBorder(16, 16, 16, 16));
        JScrollPane sp = new JScrollPane(invoiceArea);
        styleScroll(sp);
        p.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bottom.setOpaque(false);
        JComboBox<ClientDTO> cbClient = clientCombo();
        JButton btnGen  = accentBtn("📄 Generate Invoice", ACCENT);
        JButton btnMark = accentBtn("✅ Mark as Billed",   GREEN);
        bottom.add(new JLabel("Client:") {{ setForeground(TXT); }});
        bottom.add(cbClient);
        bottom.add(btnGen);
        bottom.add(btnMark);
        p.add(bottom, BorderLayout.SOUTH);

        p.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) { refreshCombo(cbClient); }
        });

        btnGen.addActionListener(e -> {
            if (cbClient.getSelectedItem() == null) { invoiceArea.setText("No clients found."); return; }
            ClientDTO c = (ClientDTO) cbClient.getSelectedItem();
            List<WorkLogDTO> logs = workLogDAO.getByClient(c.getId(), true);
            if (logs.isEmpty()) { invoiceArea.setText("No unbilled work logs for " + c.getName() + "."); return; }
            invoiceArea.setText(buildInvoiceText(c, logs));
        });

        btnMark.addActionListener(e -> {
            if (cbClient.getSelectedItem() == null) return;
            ClientDTO c = (ClientDTO) cbClient.getSelectedItem();
            if (workLogDAO.markBilled(c.getId())) {
                ok("All logs for " + c.getName() + " marked as billed!");
                invoiceArea.setText("Logs for " + c.getName() + " have been marked as BILLED.\n\nGenerate invoice again to see pending work.");
                refreshDash();
            } else err("Failed to mark logs.");
        });

        return p;
    }

    private String buildInvoiceText(ClientDTO c, List<WorkLogDTO> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════╗\n");
        sb.append("║           SCRIPTMINT FREELANCE INVOICE            ║\n");
        sb.append("╚══════════════════════════════════════════════════╝\n\n");
        sb.append(String.format("  Client Name  : %s%n", c.getName()));
        sb.append(String.format("  Hourly Rate  : ₹%d/hr%n", c.getHourlyRate()));
        sb.append("\n  ─────────────────────────────────────────────────\n");
        sb.append(String.format("  %-5s %-30s %10s%n", "Log#", "Description", "Hours"));
        sb.append("  ─────────────────────────────────────────────────\n");
        int totalHours = 0;
        for (WorkLogDTO w : logs) {
            sb.append(String.format("  %-5d %-30s %10d%n",
                    w.getLogId(), w.getDescription(), w.getHours()));
            totalHours += w.getHours();
        }
        sb.append("  ─────────────────────────────────────────────────\n");
        sb.append(String.format("  Total Hours   : %d hrs%n", totalHours));
        sb.append(String.format("  Total Amount  : ₹%d%n", totalHours * c.getHourlyRate()));
        sb.append("\n  ─────────────────────────────────────────────────\n");
        sb.append("  Status        : UNBILLED  (click Mark as Billed)\n");
        sb.append("╚══════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    /* ══════════════════════════  UPDATE CLIENT  ════════════════════ */
    private JPanel buildUpdate() {
        JPanel p = base();
        p.setLayout(new BorderLayout(0, 20));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));
        p.add(pageTitle("Update Client Details"), BorderLayout.NORTH);

        JPanel form = formCard();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        JComboBox<ClientDTO> cbClient = clientCombo();
        JTextField fName  = field("New Name");
        JTextField fRate  = field("New Hourly Rate");
        JButton    btnUpd = accentBtn("✏  Update Client", YELLOW);
        JLabel     status = statusLbl();

        // Auto-fill fields when client selected
        cbClient.addActionListener(e -> {
            if (cbClient.getSelectedItem() != null) {
                ClientDTO c = (ClientDTO) cbClient.getSelectedItem();
                fName.setText(c.getName());
                fRate.setText(String.valueOf(c.getHourlyRate()));
            }
        });

        p.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) { refreshCombo(cbClient); }
        });

        g.gridx=0; g.gridy=0; form.add(fLabel("Select Client:"),    g);
        g.gridx=1;             form.add(cbClient, g);
        g.gridx=0; g.gridy=1; form.add(fLabel("New Name:"),          g);
        g.gridx=1;             form.add(fName, g);
        g.gridx=0; g.gridy=2; form.add(fLabel("New Hourly Rate:"),   g);
        g.gridx=1;             form.add(fRate, g);
        g.gridx=0; g.gridy=3; g.gridwidth=2; form.add(btnUpd, g);
        g.gridy=4;             form.add(status, g);

        btnUpd.addActionListener(e -> {
            if (cbClient.getSelectedItem() == null) { status.setText("❌ No client selected."); status.setForeground(RED); return; }
            ClientDTO c = (ClientDTO) cbClient.getSelectedItem();
            String name = fName.getText().trim();
            String rateStr = fRate.getText().trim();
            if (name.isEmpty() || rateStr.isEmpty()) { status.setText("❌ Fill in all fields."); status.setForeground(RED); return; }
            try {
                int rate = Integer.parseInt(rateStr);
                if (rate <= 0) { status.setText("❌ Rate must be positive."); status.setForeground(RED); return; }
                c.setName(name); c.setHourlyRate(rate);
                if (clientDAO.update(c)) {
                    status.setText("✅ Client updated successfully!");
                    status.setForeground(GREEN);
                    refreshCombo(cbClient);
                } else { status.setText("❌ Update failed."); status.setForeground(RED); }
            } catch (NumberFormatException ex) { status.setText("❌ Rate must be a number."); status.setForeground(RED); }
        });

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    /* ══════════════════════════  DELETE  ═══════════════════════════ */
    private JPanel buildDelete() {
        JPanel p = base();
        p.setLayout(new BorderLayout(0, 20));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));
        p.add(pageTitle("Delete Records"), BorderLayout.NORTH);

        JPanel form = formCard();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        JComboBox<ClientDTO> cbClient = clientCombo();
        JTextField fLogId = field("Log ID (optional)");
        JButton btnDelClient = accentBtn("🗑  Delete Client (+ all logs)", RED);
        JButton btnDelLogs   = accentBtn("🗑  Delete All Logs of Client",  YELLOW);
        JButton btnDelLog    = accentBtn("🗑  Delete Specific Log by ID",  MUTED);
        JLabel  status       = statusLbl();

        p.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) { refreshCombo(cbClient); }
        });

        g.gridx=0; g.gridy=0; form.add(fLabel("Select Client:"),   g);
        g.gridx=1;             form.add(cbClient, g);
        g.gridx=0; g.gridy=1; form.add(fLabel("Specific Log ID:"), g);
        g.gridx=1;             form.add(fLogId, g);
        g.gridx=0; g.gridy=2; g.gridwidth=2; form.add(btnDelClient, g);
        g.gridy=3;             form.add(btnDelLogs, g);
        g.gridy=4;             form.add(btnDelLog, g);
        g.gridy=5;             form.add(status, g);

        btnDelClient.addActionListener(e -> {
            if (cbClient.getSelectedItem() == null) return;
            ClientDTO c = (ClientDTO) cbClient.getSelectedItem();
            int res = JOptionPane.showConfirmDialog(this,
                    "Delete client '" + c.getName() + "' and ALL their work logs?\nThis cannot be undone!",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                if (clientDAO.delete(c.getId())) {
                    status.setText("✅ Client '" + c.getName() + "' deleted."); status.setForeground(GREEN);
                    refreshCombo(cbClient); refreshDash(); refreshClientsTable();
                } else { status.setText("❌ Delete failed."); status.setForeground(RED); }
            }
        });

        btnDelLogs.addActionListener(e -> {
            if (cbClient.getSelectedItem() == null) return;
            ClientDTO c = (ClientDTO) cbClient.getSelectedItem();
            if (workLogDAO.deleteByClient(c.getId())) {
                status.setText("✅ All logs deleted for " + c.getName()); status.setForeground(GREEN);
                refreshDash();
            } else { status.setText("❌ Failed."); status.setForeground(RED); }
        });

        btnDelLog.addActionListener(e -> {
            String idStr = fLogId.getText().trim();
            if (idStr.isEmpty()) { status.setText("❌ Enter a Log ID."); status.setForeground(RED); return; }
            try {
                int lid = Integer.parseInt(idStr);
                if (workLogDAO.deleteById(lid)) {
                    status.setText("✅ Log #" + lid + " deleted."); status.setForeground(GREEN);
                    fLogId.setText(""); refreshDash();
                } else { status.setText("❌ Log ID not found."); status.setForeground(RED); }
            } catch (NumberFormatException ex) { status.setText("❌ Log ID must be a number."); status.setForeground(RED); }
        });

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    /* ══════════════════════  HELPER FACTORIES  ══════════════════════ */
    private JPanel base() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        return p;
    }

    private JPanel formCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(28, 32, 28, 32)));
        return p;
    }

    private JLabel pageTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setForeground(TXT);
        l.setBorder(new EmptyBorder(0, 0, 8, 0));
        return l;
    }

    private JLabel fLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(MUTED);
        l.setBorder(new EmptyBorder(0, 0, 0, 14));
        return l;
    }

    private JTextField field(String placeholder) {
        JTextField tf = new JTextField(22) {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(new Color(80, 96, 120));
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    g.drawString(placeholder, 8, getHeight() / 2 + 5);
                }
            }
        };
        tf.setBackground(PANEL);
        tf.setForeground(TXT);
        tf.setCaretColor(ACCENT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
        tf.setPreferredSize(new Dimension(240, 36));
        return tf;
    }

    private JButton accentBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.addMouseListener(new MouseAdapter() {
            Color orig = color;
            public void mouseEntered(MouseEvent e) { b.setBackground(orig.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(orig); }
        });
        return b;
    }

    private JLabel bigNum(String val) {
        JLabel l = new JLabel(val);
        l.setFont(new Font("Segoe UI", Font.BOLD, 38));
        return l;
    }

    private JLabel statusLbl() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(GREEN);
        return l;
    }

    private JComboBox<ClientDTO> clientCombo() {
        JComboBox<ClientDTO> cb = new JComboBox<>();
        cb.setBackground(PANEL);
        cb.setForeground(TXT);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setPreferredSize(new Dimension(240, 36));
        refreshCombo(cb);
        return cb;
    }

    private void refreshCombo(JComboBox<ClientDTO> cb) {
        cb.removeAllItems();
        for (ClientDTO c : clientDAO.getAll()) cb.addItem(c);
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(CARD);
        t.setForeground(TXT);
        t.setGridColor(BORDER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setSelectionBackground(PANEL);
        t.setSelectionForeground(ACCENT);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        JTableHeader h = t.getTableHeader();
        h.setBackground(PANEL);
        h.setForeground(ACCENT);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        return t;
    }

    private void styleScroll(JScrollPane sp) {
        sp.getViewport().setBackground(CARD);
        sp.setBorder(new LineBorder(BORDER, 1, true));
    }

    private JSeparator hLine() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setMaximumSize(new Dimension(215, 1));
        return s;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 8, 10, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private void ok(String msg)  { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void err(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
}
