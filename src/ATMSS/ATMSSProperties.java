package ATMSS;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ATMSSProperties {
    public static void main(String [] args) throws IOException {
        Properties p = new Properties();
        OutputStream os = new FileOutputStream("config.properties");

        p.setProperty("name", "ATM-SS Project");
        p.setProperty("course", "COM4107 Software Design, Development and Testing");
        p.setProperty("group", "grp_04");
        p.setProperty("member1", "Sanzhar NUSSIPBEK 18200257");
        p.setProperty("member2", "Gassyr BAKUBAY 17212189");
        p.setProperty("member3", "Anmol DHAWAN 18200228");
        p.setProperty("member4", "Oshan SHAKYA 18200206");
        p.setProperty("adminPassword", "4107");
        p.setProperty("urlPrefix", "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp04/");
        p.setProperty("dbAccountName", "comp4107_grp04");
        p.setProperty("dbName", "comp4107_grp04");
        p.setProperty("dbPassword", "comp4107_grp04");
        p.setProperty("sdk", "1.8");

        p.store(os, null);
        os.close();
    } // main

}
