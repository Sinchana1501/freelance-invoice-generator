╔══════════════════════════════════════════════════════════════════╗
║         ScriptMint — Freelance Invoice Generator                 ║
║         Swing + JDBC + MySQL  |  Ready-to-Run Project            ║
╚══════════════════════════════════════════════════════════════════╝

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 PROJECT STRUCTURE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ScriptMint/
├── src/
│   ├── DBConnection.java     ← DB credentials (edit password here)
│   ├── ClientDTO.java        ← Client data object
│   ├── WorkLogDTO.java       ← Work log data object
│   ├── ClientDAO.java        ← Client DB operations
│   ├── WorkLogDAO.java       ← Work log DB operations
│   └── ScriptMintApp.java    ← Main Swing GUI (run this)
├── scriptmint_db.sql         ← Run this in MySQL first
└── README.txt                ← You are here

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 STEP-BY-STEP SETUP
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

STEP 1 — Prerequisites
  ✔ Java JDK 8 or higher installed
  ✔ MySQL Server running
  ✔ Download MySQL Connector/J JAR:
    https://dev.mysql.com/downloads/connector/j/
    (Choose "Platform Independent" → download the .zip → extract the .jar)

STEP 2 — Create the Database
  Open MySQL command line or MySQL Workbench and run:
    source /path/to/scriptmint_db.sql
  OR paste the SQL file contents directly.

STEP 3 — Configure Password
  Open  src/DBConnection.java
  Change this line:
    private static final String PASS = "your_password";
  To your actual MySQL root password.

STEP 4 — Place the JAR
  Copy mysql-connector-j-x.x.x.jar into the ScriptMint/src/ folder.

STEP 5 — Compile
  Open terminal/cmd in the  src/  folder and run:

  Windows:
    javac -cp .;mysql-connector-j-8.x.x.jar *.java

  Mac / Linux:
    javac -cp .:mysql-connector-j-8.x.x.jar *.java

  (Replace  8.x.x  with your actual JAR version number)

STEP 6 — Run
  Windows:
    java -cp .;mysql-connector-j-8.x.x.jar ScriptMintApp

  Mac / Linux:
    java -cp .:mysql-connector-j-8.x.x.jar ScriptMintApp

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 FEATURES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  🏠 Dashboard       — Live stats: total clients, unbilled logs, revenue
  👤 Clients         — Register new clients, view all in a table
  ⏱  Log Work        — Log billable hours with description per client
  📄 Generate Invoice — View full invoice, mark logs as billed
  ✏  Update Client   — Edit client name and hourly rate (auto-fills)
  🗑  Delete Records  — Delete client, all logs, or a specific log

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ARCHITECTURE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  DTO     — Carries data between layers (ClientDTO, WorkLogDTO)
  DAO     — All DB operations (ClientDAO, WorkLogDAO)
  UI      — Swing GUI (ScriptMintApp) calls DAOs directly

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 TROUBLESHOOTING
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  Error: "No suitable driver"
    → JAR not in classpath. Double-check -cp path.

  Error: "Access denied for user root"
    → Wrong password in DBConnection.java

  Error: "Unknown database scriptmint_db"
    → Run scriptmint_db.sql in MySQL first.

  Blank table / no clients shown
    → Check DB connection and run the sample INSERT statements in the SQL file.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Good luck! — ScriptMint v2.0
