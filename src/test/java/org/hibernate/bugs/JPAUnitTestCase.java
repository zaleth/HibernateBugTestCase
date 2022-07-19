/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hibernate.bugs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
//import org.slf4j.Logger;

import org.junit.Before;
import org.junit.Test;
import se.zaleth.jar.material.MaterialCollection;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private SessionFactory sf;

        private void inspectSrcDir(File dir, ArrayList<String> names) {
            System.out.println(" Inspecting dir: " + dir.getAbsolutePath());
            for(File file : dir.listFiles()) {
                    if(file.isDirectory())
                        inspectSrcDir(file, names);
                    else {
                        String name = file.getAbsolutePath();
                        boolean found = false;
                        for(String match : names)
                            if(name.endsWith(match)) {
                                names.remove(match);
                                found = true;
                                break;
                            }
                        if(! found)
                            System.out.println("  File '" + name + "' is not mapped");
                    }
            }
        }
        
	@Before
	public void setup() {
            
            // first, verify that all classes in our HBM files exist
            File hbmDir = new File("src/main/resources/META-INF");
            System.out.println("Inspecting HBM dir: " + hbmDir.getAbsolutePath());
            ClassLoader load = ClassLoader.getSystemClassLoader();
            ArrayList<String> classNames = new ArrayList<>();
            ArrayList<String> hbmFileNames = new ArrayList<>();
            for(File file : hbmDir.listFiles()) {
                System.out.println(" Inspecting file: " + file.getName());
                hbmFileNames.add("META-INF/" + file.getName());
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String line;
                    // skip first two lines
                    in.readLine();
                    in.readLine();
                    while((line = in.readLine()) != null) {
                        // real hacky, but due to custom types we can't scan for only 'class="..."'
                        if(line.indexOf('.') > -1) {
                            // now, find the last '"' before the '.'
                            int pos = 0, newPos = -1, stop = line.indexOf('.');
                            while(newPos < stop) {
                                //System.out.println("stop: " + stop + ", newPos: " + newPos);
                                pos = newPos;
                                newPos = line.indexOf('"', newPos + 1);
                            }
                            // extract whatever's inside the quotes
                            // remember, newPos is *after* stop, so use pos instead
                            String cName = line.substring(pos+1).split("\"")[0];
                            if(! classNames.contains(cName)) {
                                System.out.println("  Testing class: " + cName);
                                Class c = load.loadClass(cName);
                                if(! c.isEnum())
                                    classNames.add(cName);
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("All HBM files checked");
            ArrayList<String> sourceNames = new ArrayList<>();
            // change class name to source name
            for(String cName : classNames) {
                String sName = cName.replace('.', File.separatorChar) + ".java";
                sourceNames.add(sName);
            }
            
            // check if any source files are not covered by HBM files
            File srcDir = new File("src/main/java");
            System.out.println("Inspecting SRC dir: " + srcDir.getAbsolutePath());
            inspectSrcDir(srcDir, sourceNames);

            // now, try to parse them
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
			// Add in any settings that are specific to your test. See resources/hibernate.properties for the defaults.
                        .configure("hibernate.cfg.xml")
			.applySetting( "hibernate.show_sql", "true" )
			.applySetting( "hibernate.format_sql", "true" )
			/*.applySetting( "hibernate.hbm2ddl.auto", "update" )*/;

                
                /*Logger log = LogManager.getLogger("org.hibernate");
                Level prev = log.getLevel();
                log.setLevel(Level.TRACE);*/
                //Configurator.setLevel("org.hibernate", Level.TRACE);
                try {
		MetadataSources metadataSources = new MetadataSources( srb.build() );
                    for(String name : hbmFileNames) {
                        System.out.println("Adding HBM file " + name);
                        metadataSources.addResource(name);
                    }
                    // Add your entities here.
                    //	.addAnnotatedClass( Foo.class )
                    Metadata metadata = metadataSources.buildMetadata();
                    sf = metadata.buildSessionFactory();
                } catch(NullPointerException e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    Configurator.setLevel("org.hibernate", Level.INFO);
                }
	}

	// Add your tests, using standard JUnit.
	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
            // No actual tests to be run, crash happens when creating the Session Factory
            Session session = sf.openSession();
            long userGroupId = 51101;
            Transaction tx = null;
            List<MaterialCollection> aColls = null;
            try {
                tx = session.beginTransaction();
                Query<MaterialCollection> q;
                String aQuery = "from MaterialCollection as coll "
                        + "where coll.userGroup = :USER_GROUP_ID and ";
                    aQuery += "coll.parentMaterialCollection is null ";
                aQuery += "order by coll.name ";
                q = session.createQuery(aQuery, MaterialCollection.class);
                q.setParameter("USER_GROUP_ID", userGroupId);
                Configurator.setLevel("org.hibernate", Level.TRACE);
                Configurator.setLevel("org.hibernate.SQL", Level.TRACE);
                aColls = q.list();
                tx.commit();
            } catch (RuntimeException e) {
                tx.rollback();
                throw e;
            } finally {
                Configurator.setLevel("org.hibernate", Level.INFO);
                Configurator.setLevel("org.hibernate.SQL", Level.INFO);
                session.close();
            }
	}
}
