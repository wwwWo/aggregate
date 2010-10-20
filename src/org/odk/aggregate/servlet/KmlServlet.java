/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.aggregate.servlet;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.aggregate.EMFactory;
import org.odk.aggregate.constants.ServletConsts;
import org.odk.aggregate.exception.ODKFormNotFoundException;
import org.odk.aggregate.exception.ODKIncompleteSubmissionData;
import org.odk.aggregate.table.SubmissionKml;

/**
 * Servlet to generate a KML file for download
 *
 * @author alerer@gmail.com, wbrunette@gmail.com
 *
 */
public class KmlServlet extends ServletUtilBase {

  private static final String INVALID_NUM_SUBMISSIONS_FORMAT = "ERROR! INVALID NUMBER OF SUBMISSIONS FORMAT";

  public static final String IMAGE_FIELD = "imageField";

  public static final String TITLE_FIELD = "titleField";

  public static final String GEOPOINT_FIELD = "geopointField";

  public static final String NUMBER_SUBMISSIONS = "numberSubmissions";
  
  /**
   * Serial number for serialization
   */
  private static final long serialVersionUID = 2387155275645640699L;
  
  /**
   * URI from base
   */
  public static final String ADDR = "kml";

  /**
   * Handler for HTTP Get request that responds with CSV
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    // verify user is logged in
    if (!verifyCredentials(req, resp)) {
      return;
    }
   
    // get parameter
    String odkId = getParameter(req, ServletConsts.ODK_ID);
    
    String geopointField = getParameter(req, GEOPOINT_FIELD);
    String titleField = getParameter(req, TITLE_FIELD);
    String imageField = getParameter(req, IMAGE_FIELD);
    String numSubmissionStr = getParameter(req, NUMBER_SUBMISSIONS);
    
    if(odkId == null || geopointField == null) {
      errorMissingKeyParam(resp);
      return;
    }

    int maxSubmissions = 100;
    
    try {
      if(numSubmissionStr != null) {
        maxSubmissions = Integer.parseInt(numSubmissionStr);
      }
    } catch (NumberFormatException e1) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_NUM_SUBMISSIONS_FORMAT);
      return;
    }
    
    EntityManager em = EMFactory.get().createEntityManager();

    try {
      resp.setContentType(ServletConsts.RESP_TYPE_ENRICHED);
      setDownloadFileName(resp, odkId + ServletConsts.KML_FILENAME_APPEND);

      // create KML
      SubmissionKml submissions = new SubmissionKml(odkId, req.getServerName(), em, geopointField, titleField, imageField, maxSubmissions);
      submissions.generateKml(resp.getWriter());
    } catch (ODKFormNotFoundException e) {
      odkIdNotFoundError(resp);
    }catch (ODKIncompleteSubmissionData e) {
      errorRetreivingData(resp);
    } finally {
      em.close();
    }
  }
  


}
