package com.bids.bpm.jee.util;

import java.io.File;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BPM_LOG_DIR_PROP_NAME;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 * 
 * <p>
 * Example injection on a managed bean field:
 * </p>
 * 
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
public class Resources {
   // use @SuppressWarnings to tell IDE to ignore warnings about field not being referenced directly
   @SuppressWarnings("unused")
   @Produces
   @PersistenceContext
   @BidsEjbEM
   private EntityManager em;
   
   @Produces
   public Logger produceLog(InjectionPoint injectionPoint) {
      return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
   }

   @Produces
   @GlobalLogDir
   public File produceBaseLoggingDir(){
       String baseDirStr = new File(System.getProperty("user.home"), "data/jbpm").toString();
       if ( System.getProperty(BIDS_BPM_LOG_DIR_PROP_NAME) != null )
           baseDirStr = System.getProperty(BIDS_BPM_LOG_DIR_PROP_NAME);
       File baseDir = new File(baseDirStr);
       baseDir.mkdirs();
       return baseDir;
   }
}
