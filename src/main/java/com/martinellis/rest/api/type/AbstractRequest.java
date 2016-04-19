package com.martinellis.rest.api.type;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.api.exceptions.CustomErrorException;
import com.martinellis.rest.api.exceptions.CustomErrorExceptionBuilder;

/**
 *
 * @author Level Up Analytics <acxiom at levelupanalytics.com>
 */
public abstract class AbstractRequest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private MultivaluedMap<String, String> parameters;
    private Map<String, String> errors;

    public AbstractRequest() {
    }

    public AbstractRequest(MultivaluedMap<String, String> parameters) {
        this.parameters = parameters;
        errors = new HashMap<String, String>();
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public void throwValidationErrors() {

        if (!errors.isEmpty()) {
            CustomErrorExceptionBuilder errorBuilder = new CustomErrorExceptionBuilder();
            for (Entry<String, String> entry : errors.entrySet()) {
                errorBuilder.validationError(entry.getKey(), entry.getValue());
            }
            CustomErrorException ex = errorBuilder.build();
            logger.info("Validation failed. Throwing Exception: ", ex);
            throw ex;
        }
    }

    protected java.sql.Date getDate(String field) {
        return this.getDate(field, false);
    }

    protected java.sql.Date getDate(String field, boolean mandatory) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            if (mandatory) {
                this.errors.put(field, "is not optional and missing");
            }
            return null;
        }
        if (value.isEmpty()) {
            this.errors.put(field, "is empty");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //sdf.setTimeZone(TimeZone.getTimeZone("PDT"));
            //sdf.setLenient(false);

            Date testDate = null;
            testDate = sdf.parse(value);
            if (!sdf.format(testDate).equals(value)) {
                this.errors.put(field, "is invalid and could not be parsed");
                return null;
            } else {
                return new java.sql.Date(sdf.parse(value).getTime());
            }
        } catch (ParseException ex) {
            logger.error(null, ex);
            this.errors.put(field, "is invalid and could not be parsed: " + ex);
            return null;
        } catch (IllegalArgumentException ex) {
            logger.error(null, ex);
            this.errors.put(field, "is invalid and could not be parsed: " + ex);
            return null;
        }
    }

    protected List<Integer> getIntList(String field) {
        return this.getIntList(field, false);
    }

    protected List<Integer> getIntList(String field, boolean mandatory) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            if (mandatory) {
                this.errors.put(field, "is not optional and missing");
            }
            return new ArrayList<Integer>();
        }
        if (value.isEmpty()) {
            this.errors.put(field, "is empty");
            return new ArrayList<Integer>();
        }
        try {
            String[] parts = value.split(",");
            List<Integer> result = new ArrayList<Integer>();
            for (String s : parts) {
                result.add(Integer.parseInt(s));
            }
            return result;
        } catch (NumberFormatException ex) {
            logger.error(null, ex);
            this.errors.put(field, "is invalid and could not be parsed: " + ex);
            return new ArrayList<Integer>();
        }
    }

    public Boolean getBoolean(String field) {
        return getBoolean(field, false);
    }

    public Boolean getBoolean(String field, boolean mandatory) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            if (mandatory) {
                this.errors.put(field, "is not optional and missing");
            }
            return null;
        }
        if (value != null && value.isEmpty()) {
            this.errors.put(field, "is empty");
            return null;
        }

        try {
            return new Boolean(value);
        } catch (Exception e) {
            this.errors.put("field", "could not be coerced into a boolean value");
            return null;
        }
    }

    protected Integer getInt(String field) {
        return this.getInt(field, false);
    }

    protected Integer getInt(String field, boolean mandatory) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            if (mandatory) {
                this.errors.put(field, "is not optional and missing");
            }
            return null;
        }
        if (value.isEmpty()) {
            this.errors.put(field, "is empty");
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            logger.error(null, ex);
            this.errors.put(field, "is invalid could not be parsed");
            return null;
        }
    }

    protected Long getLong(String field) {
        return this.getLong(field, false);
    }

    protected Long getLong(String field, boolean mandatory) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            if (mandatory) {
                this.errors.put(field, "is not optional and missing");
            }
            return null;
        }
        if (value.isEmpty()) {
            this.errors.put(field, "is empty");
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            logger.error(null, ex);
            this.errors.put(field, "is invalid could not be parsed: " + ex);
            return null;
        }
    }

    protected String getString(String field, int maxLength) {
        return this.getString(field, false, maxLength);

    }

    protected String getString(String field, boolean mandatory, int maxLength) {
        String result = this.getString(field, mandatory);
        if (result.length() > maxLength) {
            this.errors.put(field, "is too long (max " + maxLength + " characters allowed)");
        }
        return result;
    }

    protected String getString(String field) {
        return this.getString(field, false);
    }

    protected final String getString(String field, boolean mandatory) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            if (mandatory) {
                this.errors.put(field, "is not optional and missing");
            }
            return "";
        }
        if (value.isEmpty()) {
            if(mandatory) {
                this.errors.put(field, "is empty");
                return "";
            }
            return "";
        }
        
        try {
            //urldecode
            value = URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.debug("Could not urldecode input", ex);
        } catch (IllegalArgumentException ex) {
        }

        return value;
    }

    protected List<String> getStringList(String field) {
        return this.getStringList(field, false);
    }

    protected List<String> getStringList(String field, boolean mandatory) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            if (mandatory) {
                this.errors.put(field, "is not optional and missing");
            }
            return new ArrayList<String>();
        }
        if (value.isEmpty()) {
            this.errors.put(field, "is empty");
            return new ArrayList<String>();
        }
        ArrayList<String> values = new ArrayList<String>();
        String[] split = value.split(",");
        for (String argument : split) {
            values.add(argument.trim());
        }
        return values;
    }

    protected UUID getUuid(String field) {
        return getUuid(field, false);
    }

    protected UUID getUuid(String field, boolean required) {
        if (required && !parameterPresentAndNotEmpty(field)) {
            return null;
        }
        try {
            String value = parameters.getFirst(field);
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            logger.error(null, ex);
            this.errors.put(field, "is invalid and could not be parsed: " + ex);
            return null;
        }
    }

    private boolean parameterPresentAndNotEmpty(String field) {
        return assertParameterPresent(field) && assertParameterEmpty(field);
    }

    private boolean assertParameterPresent(String field) {
        String value = this.parameters.getFirst(field);
        if (value == null) {
            this.errors.put(field, "is not optional and missing");
        }
        return value != null;
    }

    private boolean assertParameterEmpty(String field) {
        String value = this.parameters.getFirst(field);
        if (value.isEmpty()) {
            this.errors.put(field, "is empty");
        }
        return !value.isEmpty();
    }

    public MultivaluedMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(MultivaluedMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
        hash = 47 * hash + (this.errors != null ? this.errors.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractRequest other = (AbstractRequest) obj;
        if (this.parameters != other.parameters && (this.parameters == null || !this.parameters.equals(other.parameters))) {
            return false;
        }
        if (this.errors != other.errors && (this.errors == null || !this.errors.equals(other.errors))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AbstractInput{" + "parameters=" + parameters + ", errors=" + errors + '}';
    }
}