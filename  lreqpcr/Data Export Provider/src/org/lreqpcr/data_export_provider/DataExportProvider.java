/*
 * Copyright (C) 2013   Bob Rutledge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * and open the template in the editor.
 */
package org.lreqpcr.data_export_provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jxl.write.WriteException;
import org.lreqpcr.core.data_objects.AverageCalibrationProfile;
import org.lreqpcr.core.data_objects.AverageProfile;
import org.lreqpcr.core.data_objects.AverageSampleProfile;
import org.lreqpcr.core.data_objects.CalibrationProfile;
import org.lreqpcr.core.data_objects.Profile;
import org.lreqpcr.core.data_objects.Run;
import org.lreqpcr.core.data_objects.SampleProfile;
import org.lreqpcr.data_export_services.DataExportServices;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Bob Rutledge
 */
@ServiceProvider(service=DataExportServices.class)
public class DataExportProvider implements DataExportServices {

    public void exportAverageSampleProfilesFromRuns(List<Run> runList) {
//Defer to HashMap exportation of average profiles in which only the run name is used from the Run
        HashMap<String, List<SampleProfile>> map = Maps.newHashMap();
        for(Run run : runList){
           //Must cast the Run AverageProfileList to AverageSampleProfiles
            List<SampleProfile> avSampleList = Lists.newArrayList();
            for (AverageProfile avProfile : run.getAverageProfileList()){
                AverageSampleProfile avSampleProfile = (AverageSampleProfile) avProfile;
                avSampleList.add(avSampleProfile);
            }
            map.put(run.getName(), avSampleList);
        }
        exportSampleProfiles(map);
    }

    public void exportReplicateSampleProfilesFromRuns(List<Run> runList) {
//Defer to HashMap exportation of replicate sample profiles in which only the run name is used from the Run
        //Run name, 
        HashMap<String, List<SampleProfile>> map = new HashMap<String, List<SampleProfile>>();
        for (Run run : runList){
            List<SampleProfile> samplePrfList = Lists.newArrayList();
            for (AverageProfile avProfile : run.getAverageProfileList()){
                for(Profile profile : avProfile.getReplicateProfileList()){
                    SampleProfile sampleProfile = (SampleProfile) profile;
                    samplePrfList.add(sampleProfile);
                }
            }
            map.put(run.getName(),samplePrfList);
        }
        exportSampleProfiles(map);
    }
    
    private void exportSampleProfiles(HashMap<String, List<SampleProfile>> groupList){
         try {
            SampleProfileExcelDataExport.exportProfiles(groupList);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void exportAverageSampleProfiles(HashMap<String, List<AverageSampleProfile>> groupList) {
        try {
//Need to convert to HashMap with List<SampleProfile> in order to use the generic SampleProfile Excel export function
            HashMap<String, List<SampleProfile>> sampleProfileMap = new HashMap<String, List<SampleProfile>>();
            List<String> keyList = new ArrayList<String>(groupList.keySet());
            for (String key : keyList){
                List<AverageSampleProfile> avPrfList = groupList.get(key);
                //Convert to a list of SampleProfiles
                List<SampleProfile> samplePrfList = new ArrayList<SampleProfile>();
                for (AverageSampleProfile prf : avPrfList){
                    samplePrfList.add(prf);
                }
                sampleProfileMap.put(key, samplePrfList);
            }
            SampleProfileExcelDataExport.exportProfiles(sampleProfileMap);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void exportReplicateSampleProfiles(HashMap<String, List<SampleProfile>> groupList) {
        try {
            SampleProfileExcelDataExport.exportProfiles(groupList);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void exportAverageCalibrationProfiles(HashMap<String, List<AverageCalibrationProfile>> groupList) {
        try {
//Need to convert to HashMap with List<SampleProfile> in order to use the generic SampleProfile Excel export function
            HashMap<String, List<CalibrationProfile>> calibrationProfileMap = new HashMap<String, List<CalibrationProfile>>();
            List<String> keyList = new ArrayList<String>(groupList.keySet());
            for (String key : keyList){
                List<AverageCalibrationProfile> avPrfList = groupList.get(key);
                //Convert to a list of SampleProfiles
                List<CalibrationProfile> calPrfList = new ArrayList<CalibrationProfile>();
                for (AverageCalibrationProfile prf : avPrfList){
                    calPrfList.add(prf);
                }
                calibrationProfileMap.put(key, calPrfList);
            }
            CalibrationProfileExcelDataExport.exportProfiles(calibrationProfileMap);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void exportReplicateCalibrationProfiles(HashMap<String, List<CalibrationProfile>> groupList) {
        try {
            CalibrationProfileExcelDataExport.exportProfiles(groupList);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (WriteException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void exportAverageCalibrationProfiles(List<AverageCalibrationProfile> profileList) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void exportReplicateCalibrationProfiles(List<AverageCalibrationProfile> profileList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
