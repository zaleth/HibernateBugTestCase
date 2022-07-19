/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.util;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import oracle.jdbc.OraclePreparedStatement;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 *
 * @author joel
 */
public class UTFStringType implements UserType, ParameterizedType {

    private static final int[] SQL_TYPES = new int[]{Types.VARCHAR};

    @Override
    //public Object nullSafeGet(ResultSet rs, String[] strings, SessionImplementor si, Object o) throws HibernateException, SQLException {
    public Object nullSafeGet(ResultSet rs, String[] strings, SharedSessionContractImplementor si, Object o) throws HibernateException, SQLException {    
    //public String nullSafeGet(ResultSet rs, int i, SharedSessionContractImplementor si, Object o) throws HibernateException, SQLException {
        return nullSafeGet(rs, strings, o);
    }

    @Override
    //public void nullSafeSet(PreparedStatement ps, Object o, int i, SessionImplementor si) throws HibernateException, SQLException {
    public void nullSafeSet(PreparedStatement ps, Object o, int i, SharedSessionContractImplementor si) throws HibernateException, SQLException {
    //public void nullSafeSet(PreparedStatement ps, String o, int i, SharedSessionContractImplementor si) throws HibernateException, SQLException {
        nullSafeSet(ps, o, i);
    }

    public void setParameterValues(Properties parameters) {
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
        String aValue = rs.getString(names[0]);
    /*public String nullSafeGet(ResultSet rs, int i, Object owner) throws SQLException {
        String aValue = rs.getString(i);*/
        if (rs.wasNull()) {
            return null;
        }
        return aValue;
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.NUMERIC);
        }
        if (statement instanceof OraclePreparedStatement) {
            ((OraclePreparedStatement) statement).setFormOfUse(index, OraclePreparedStatement.FORM_NCHAR);
        }
        statement.setString(index, (String) value);
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return String.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
    //public boolean equals(String x, String y) throws HibernateException {
        if (x == null && y == null) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return (x.equals(y));
    }

    public int hashCode(Object x) throws HibernateException {
    //public int hashCode(String x) throws HibernateException {
        return x.hashCode();
    }

    public Object deepCopy(Object value) throws HibernateException {
    //public String deepCopy(String value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
    //public Serializable disassemble(String value) throws HibernateException {
        return (Serializable) value;
    }

    public String assemble(Serializable cached, Object owner) throws HibernateException {
        return cached.toString();
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
    //public String replace(String original, String target, Object owner) throws HibernateException {
        return original;
    }

    /*@Override
    public int getSqlType() {
        return SQL_TYPES[0];
    }*/

}
