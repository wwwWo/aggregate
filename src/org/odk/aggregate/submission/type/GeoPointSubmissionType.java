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

package org.odk.aggregate.submission.type;


import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.odk.aggregate.constants.BasicConsts;
import org.odk.aggregate.constants.PersistConsts;
import org.odk.aggregate.exception.ODKConversionException;
import org.odk.aggregate.form.Form;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.JsonObject;

public class GeoPointSubmissionType extends SubmissionFieldBase<GeoPoint> {
  
  private GeoPoint coordinates;
  
  /**
   * Constructor 
   * 
   * @param propertyName
   *    Name of submission element 
   */
  public GeoPointSubmissionType(String propertyName) {
    super(propertyName, false);
  }

  @Override
  public void setValueFromString(String value) throws ODKConversionException {
    String [] values = value.split("\\s+");
    if(values.length == 2) {
    coordinates = new GeoPoint(Double.valueOf(values[0]), Double.valueOf(values[1]));    
    } else if(values.length == 3) {
    coordinates = new GeoPoint(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]));    
    } else if(values.length == 4) {
      coordinates = new GeoPoint(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]), Double.valueOf(values[3]));    
    } else {
      throw new ODKConversionException("Problem with GPS Coordinates being parsed from XML");
    }
  }

  @Override
  public GeoPoint getValue() {
    return coordinates;
  }

  @Override
  public void addValueToEntity(Entity dbEntity) {
    if(coordinates == null) {
      return; // nothing to add
    }
    dbEntity.setProperty(propertyName + PersistConsts.LATITUDE_PROPERTY, coordinates.getLatitude());
    dbEntity.setProperty(propertyName + PersistConsts.LONGITUDE_PROPERTY, coordinates.getLongitude());
    dbEntity.setProperty(propertyName + PersistConsts.ALTITUDE_PROPERTY, coordinates.getAltitude());
    dbEntity.setProperty(propertyName + PersistConsts.ACCURACY_PROPERTY, coordinates.getAccuracy());
  }

  /**
   * Add submission field value to JsonObject
   * @param JSON Object to add value to
   */  
  @Override
  public void addValueToJsonObject(JsonObject jsonObject, List<String> propertyNames) {
    if(!propertyNames.contains(propertyName)){
      return;
    }
    if(coordinates != null) {
      jsonObject.addProperty(propertyName + PersistConsts.LATITUDE_PROPERTY, coordinates.getLatitude());
      jsonObject.addProperty(propertyName + PersistConsts.LONGITUDE_PROPERTY, coordinates.getLongitude());
      jsonObject.addProperty(propertyName + PersistConsts.ALTITUDE_PROPERTY, coordinates.getAltitude());
      jsonObject.addProperty(propertyName + PersistConsts.ACCURACY_PROPERTY, coordinates.getAccuracy());
    }
  }
  
  @Override
  public void addValueToXmlSerialization(StringBuilder b) {
	  GeoPoint value = getValue();
	  if ( value != null ) {
		  b.append("<" + propertyName + ">");
		  b.append(StringEscapeUtils.escapeXml(value.getLatitude().toString()));
		  b.append(" ");
		  b.append(StringEscapeUtils.escapeXml(value.getLongitude().toString()));
		  if ( value.getAltitude() != null ) {
			  b.append(" ");
			  b.append(StringEscapeUtils.escapeXml(value.getAltitude().toString()));
		  }
		  if ( value.getAccuracy() != null ) {
			  b.append(" ");
			  b.append(StringEscapeUtils.escapeXml(value.getAccuracy().toString()));
		  }
		  b.append("</" + propertyName + ">");
	  } else {
		  b.append("<" + propertyName + "/>");
	  }
  }

  @Override
  public void addValueToXmlAttachmentSerialization(StringBuilder b, String baseServerUrl) {
  }

  @Override
  public void getValueFromEntity(Entity dbEntity, Form form) {
    Double latCoor = (Double) dbEntity.getProperty(propertyName + PersistConsts.LATITUDE_PROPERTY);
    Double longCoor = (Double) dbEntity.getProperty(propertyName + PersistConsts.LONGITUDE_PROPERTY);
    Double altitude = (Double) dbEntity.getProperty(propertyName + PersistConsts.ALTITUDE_PROPERTY);
    Double accuracy = (Double) dbEntity.getProperty(propertyName + PersistConsts.ACCURACY_PROPERTY);
    coordinates = new GeoPoint(latCoor, longCoor, altitude, accuracy);    
  }
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GeoPointSubmissionType)) {
      return false;
    }
    // super will compare value
    if (!super.equals(obj)) {
      return false;
    }
    
    GeoPointSubmissionType other = (GeoPointSubmissionType) obj;
    return (coordinates == null ? (other.coordinates == null) : (coordinates.equals(other.coordinates)));
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode() + (coordinates == null ? 0 : coordinates.hashCode());
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return super.toString()
      + BasicConsts.TO_STRING_DELIMITER + (getValue() != null ? getValue() : BasicConsts.EMPTY_STRING);
  }

}
