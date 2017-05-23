package com.airbnb.epoxy;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

import static com.airbnb.epoxy.Utils.isViewClickListenerType;

abstract class AttributeInfo {

  protected String fieldName;
  protected TypeName typeName;
  protected TypeMirror typeMirror;
  protected String modelName;
  protected String modelPackageName;
  protected boolean useInHash;
  protected boolean ignoreRequireHashCode;
  protected boolean doNotUseInToString;
  protected boolean generateSetter;
  protected List<AnnotationSpec> setterAnnotations = new ArrayList<>();
  protected boolean generateGetter;
  protected List<AnnotationSpec> getterAnnotations = new ArrayList<>();
  protected boolean hasFinalModifier;
  protected boolean packagePrivate;
  protected CodeBlock javaDoc;

  /** If this attribute is in an attribute group this is the name of the group. */
  String groupKey;
  List<AttributeInfo> overloadedAttributesInSameGroup;

  /**
   * Track whether there is a setter method for this attribute on a super class so that we can call
   * through to super.
   */
  protected boolean hasSuperSetter;
  // for private fields (Kotlin case)
  protected boolean isPrivate;
  protected String getterMethodName;

  protected String setterMethodName;

  /**
   * True if this attribute is completely generated as a field on the generated model. False if it
   * exists as a user defined attribute in a model super class.
   */
  protected boolean isGenerated;
  /** If {@link #isGenerated} is true, a default value for the field can be set here. */
  protected CodeBlock defaultValue;
  /**
   * If {@link #isGenerated} is true, this represents whether null is a valid value to set on the
   * attribute. If this is true, then the {@link #defaultValue} should be null unless a different
   * default value is explicitly set.
   * <p>
   * This is Boolean to have null represent that nullability was not explicitly set, eg for
   * primitives or legacy attributes that weren't made with nullability support in mind.
   */
  protected Boolean isNullable;

  protected void setJavaDocString(String docComment) {
    if (docComment != null && !docComment.trim().isEmpty()) {
      javaDoc = CodeBlock.of(docComment);
    } else {
      javaDoc = null;
    }
  }

  boolean isRequired() {
    return isGenerated && defaultValue == null;
  }

  String getFieldName() {
    return fieldName;
  }

  TypeName getTypeName() {
    return typeName;
  }

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  boolean useInHash() {
    return useInHash;
  }

  boolean ignoreRequireHashCode() {
    return ignoreRequireHashCode;
  }

  public boolean doNotUseInToString() {
    return doNotUseInToString;
  }

  boolean generateSetter() {
    return generateSetter;
  }

  List<AnnotationSpec> getSetterAnnotations() {
    return setterAnnotations;
  }

  boolean generateGetter() {
    return generateGetter;
  }

  List<AnnotationSpec> getGetterAnnotations() {
    return getterAnnotations;
  }

  boolean hasSuperSetterMethod() {
    return hasSuperSetter;
  }

  boolean hasFinalModifier() {
    return hasFinalModifier;
  }

  boolean isPackagePrivate() {
    return packagePrivate;
  }

  String getterCode() {
    return isPrivate ? getterMethodName + "()" : fieldName;
  }

  // Special case to avoid generating recursive getter if field and its getter names are the same
  String superGetterCode() {
    return isPrivate ? String.format("super.%s()", getterMethodName) : fieldName;
  }

  String setterCode() {
    return (isGenerated ? "this." : "super.")
        + (isPrivate ? setterMethodName + "($L)" : fieldName + " = $L");
  }

  String generatedSetterName() {
    return fieldName;
  }

  String generatedGetterName() {
    return fieldName;
  }

  @Override
  public String toString() {
    return "Attribute {"
        + "model='" + modelName + '\''
        + ", name='" + fieldName + '\''
        + ", type=" + typeName
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AttributeInfo)) {
      return false;
    }

    AttributeInfo that = (AttributeInfo) o;

    if (!fieldName.equals(that.fieldName)) {
      return false;
    }
    return typeName.equals(that.typeName);
  }

  @Override
  public int hashCode() {
    int result = fieldName.hashCode();
    result = 31 * result + typeName.hashCode();
    return result;
  }

  boolean isViewClickListener() {
    return isViewClickListenerType(getTypeMirror());
  }

  String getModelClickListenerName() {
    return getFieldName() + GeneratedModelWriter.GENERATED_FIELD_SUFFIX;
  }

  String getModelName() {
    return modelName;
  }

  String getPackageName() {
    return modelPackageName;
  }

  void setAttributesInSameGroup(List<AttributeInfo> attributes) {
    overloadedAttributesInSameGroup = attributes;
  }

  boolean isOverload() {
    return overloadedAttributesInSameGroup != null && !overloadedAttributesInSameGroup.isEmpty();
  }
}
