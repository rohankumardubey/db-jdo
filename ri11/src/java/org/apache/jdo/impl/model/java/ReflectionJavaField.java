/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.apache.jdo.impl.model.java;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;

import org.apache.jdo.impl.model.java.AbstractJavaField;
import org.apache.jdo.model.ModelFatalException;
import org.apache.jdo.model.java.JavaField;
import org.apache.jdo.model.java.JavaType;
import org.apache.jdo.model.jdo.JDOField;
import org.apache.jdo.util.I18NHelper;

/**
 * This class provides a basic JavaField implementation using a reflection
 * Field instance. The implementation supports lazy initialization of the
 * wrapped reflection field instance (see 
 * {@link #ReflectionJavaField(String fieldName, JavaType declaringClass)}.
 * <p>
 * Note, this implementation is not connected to a JavaModelFactory, thus
 * it can only support predefined types as field types.
 * @see PredefinedType
 * @author Michael Bouschen
 * @since JDO 1.0.1
 */
public class ReflectionJavaField
    extends AbstractJavaField
{
    /** The wrapped java.lang.reflect.Field instance. */
    private Field field;

    /** The type of the field. */
    protected JavaType type;

    /** I18N support */
    private final static I18NHelper msg = 
        I18NHelper.getInstance(ReflectionJavaField.class);

    /** 
     * Constructor taking a reflection field representation. The specifie
     * field must not be <code>null</code>. 
     * @param field the java.lang.reflect.Field instance
     * @param declaringClass the JavaType of the declaring class or interface.
     */
    protected ReflectionJavaField(Field field, JavaType declaringClass)
    {
        super((field == null) ? null : field.getName(), declaringClass);
        if (field == null)
            throw new ModelFatalException(msg.msg(
                "ERR_InvalidNullFieldInstance", "ReflectionJavaField.<init>")); //NOI18N
        this.field = field;
    }
    
    /** 
     * Constructor taking the field name. This constructor allows lazy
     * initialization of the field reference. 
     * @param fieldName the name of the field.
     * @param declaringClass the JavaType of the declaring class or interface.
     */
    protected ReflectionJavaField(String fieldName, JavaType declaringClass)
    {
        super(fieldName, declaringClass);
    }

    /**
     * Returns the Java language modifiers for the field represented by
     * this JavaField, as an integer. The java.lang.reflect.Modifier class
     * should be used to decode the modifiers. 
     * @return the Java language modifiers for this JavaField
     * @see java.lang.reflect.Modifier
     */
    public int getModifiers()
    {
        ensureInitializedField();
        return field.getModifiers();
    }

    /**
     * Returns the JavaType representation of the field type.
     * @return field type
     */
    public JavaType getType()
    {
        if (type == null) {
            ensureInitializedField();
            String typeName = field.getType().getName();
            // Note, this only checks for predefined types!
            type = PredefinedType.getPredefinedType(typeName);
        }
        return type;
    }
    
    // ===== Methods not defined in JavaField =====

    /** 
     * Returns the java.lang.reflect.Field that is wrapped by this
     * JavaField.
     * @return the java.lang.reflect.Field instance.
     */
    protected Field getField()
    {
        ensureInitializedField();
        return this.field;
    }

    /**
     * Helper method to retrieve the java.lang.reflect.Field for this
     * JavaField.
     * @param clazz the Class instance of the declaring class or interface
     * @param fieldName the field name
     */
    public static Field getDeclaredFieldPrivileged(final Class clazz, 
                                                   final String fieldName)
    {
        if ((clazz == null) || (fieldName == null))
            return null;

        return (Field) AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run () {
                    try {
                        return clazz.getDeclaredField(fieldName);
                    }
                    catch (SecurityException ex) {
                        throw new ModelFatalException(
                            msg.msg("EXC_CannotGetDeclaredField", //NOI18N
                                    clazz.getName()), ex); 
                    }
                    catch (NoSuchFieldException ex) {
                        return null; // do nothing, just return null
                    }
                    catch (LinkageError ex) {
                        throw new ModelFatalException(msg.msg(
                           "EXC_ClassLoadingError", clazz.getName(), //NOI18N
                           ex.toString()));
                    }
                }
            }
            );
    }

    // ===== Internal helper methods =====
    
    /**
     * This method makes sure the reflection field is set.
     */
    protected void ensureInitializedField()
    {
        if (this.field == null) {
            this.field = getDeclaredFieldPrivileged(
                ((ReflectionJavaType)getDeclaringClass()).getJavaClass(),
                getName());
            if (field == null) {
                throw new ModelFatalException(msg.msg(
                    "ERR_MissingFieldInstance", //NOI18N
                    "ReflectionJavaField.ensureInitializedField", getName())); //NOI18N
            }
        }
    }

}