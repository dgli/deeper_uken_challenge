/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.util.generator;

/**
 *
 * This class represent a parameter configuration. There are multiple
 * TypeInfos in case of multityped parameters.
 *
 * @author elias_naur <elias_naur@users.sourceforge.net>
 * @version $Revision: 3412 $
 * $Id: TypeInfo.java 3412 2010-09-26 23:43:24Z spasi $
 */

import org.lwjgl.util.generator.opengl.GLvoid;

import com.sun.mirror.declaration.*;
import com.sun.mirror.type.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.nio.*;

public class TypeInfo {
	public static final String UNSIGNED_PARAMETER_NAME = "unsigned";

	private final Signedness signedness;
	private final Class type;
	private final String auto_type;

	private TypeInfo(Class type, Signedness signedness, String auto_type) {
		this.type = type;
		this.signedness = signedness;
		this.auto_type = auto_type;
	}

	public Class getType() {
		return type;
	}

	public Signedness getSignedness() {
		return signedness;
	}

	public String getAutoType() {
		if (auto_type == null)
			throw new RuntimeException("No auto type assigned");
		return auto_type;
	}

	private static Class getTypeFromPrimitiveKind(PrimitiveType.Kind kind) {
		Class type;
		switch (kind) {
			case LONG:
				type = long.class;
				break;
			case INT:
				type = int.class;
				break;
			case FLOAT:
				type = float.class;
				break;
			case DOUBLE:
				type = double.class;
				break;
			case SHORT:
				type = short.class;
				break;
			case BYTE:
				type = byte.class;
				break;
			case BOOLEAN:
				type = boolean.class;
				break;
			default:
				throw new RuntimeException(kind + " is not allowed");
		}
		return type;
	}

	private static Class getBufferTypeFromPrimitiveKind(PrimitiveType.Kind kind) {
		Class type;
		switch (kind) {
			case INT:
				type = IntBuffer.class;
				break;
			case FLOAT:
				type = FloatBuffer.class;
				break;
			case DOUBLE:
				type = DoubleBuffer.class;
				break;
			case SHORT:
				type = ShortBuffer.class;
				break;
			case LONG:
				type = LongBuffer.class;
				break;
			case BYTE: /* fall through */
			case BOOLEAN:
				type = ByteBuffer.class;
				break;
			default:
				throw new RuntimeException(kind + " is not allowed");
		}
		return type;
	}

	private static TypeInfo getDefaultTypeInfo(TypeMirror t) {
		Class java_type = Utils.getJavaType(t);
		return new TypeInfo(java_type, Signedness.NONE, null);
	}

	public static Map<ParameterDeclaration, TypeInfo> getDefaultTypeInfoMap(MethodDeclaration method) {
		Map<ParameterDeclaration, TypeInfo> map = new HashMap<ParameterDeclaration, TypeInfo>();
		for (ParameterDeclaration param : method.getParameters()) {
			TypeInfo type_info = getDefaultTypeInfo(param.getType());
			map.put(param, type_info);
		}
		return map;
	}

	private static Collection<TypeInfo> getTypeInfos(TypeMap type_map, Declaration param, TypeMirror decl_type) {
		Collection<AnnotationMirror> annotations = Utils.getSortedAnnotations(param.getAnnotationMirrors());
		Map<Class, TypeInfo> types = new HashMap<Class, TypeInfo>();
		Collection<TypeInfo> multityped_result = new ArrayList<TypeInfo>();
		boolean add_default_type = true;
		for (AnnotationMirror annotation : annotations) {
			NativeType native_type_annotation = NativeTypeTranslator.getAnnotation(annotation, NativeType.class);
			if (native_type_annotation != null) {
				Class<? extends Annotation> annotation_type = NativeTypeTranslator.getClassFromType(annotation.getAnnotationType());
				Signedness signedness = type_map.getSignednessFromType(annotation_type);
				Class inverse_type = type_map.getInverseType(annotation_type);
				String auto_type = type_map.getAutoTypeFromAnnotation(annotation);
				if (inverse_type != null) {
					if (types.containsKey(inverse_type)) {
						TypeInfo inverse_type_info = types.get(inverse_type);
						String inverse_auto_type = inverse_type_info.getAutoType();
						auto_type = signedness == Signedness.UNSIGNED ? auto_type + " : " + inverse_auto_type :
							inverse_auto_type + " : " + auto_type;
						auto_type = UNSIGNED_PARAMETER_NAME + " ? " + auto_type;
						signedness = Signedness.BOTH;
						types.remove(inverse_type);
						multityped_result.remove(inverse_type_info);
					}
				}
				Class type;
				PrimitiveType.Kind kind;
				GLvoid void_annotation = param.getAnnotation(GLvoid.class);
				kind = void_annotation == null ? type_map.getPrimitiveTypeFromNativeType(annotation_type) : void_annotation.value();
				if (Utils.getNIOBufferType(decl_type) != null)
					type = getBufferTypeFromPrimitiveKind(kind);
				else
					type = getTypeFromPrimitiveKind(kind);
				TypeInfo type_info = new TypeInfo(type, signedness, auto_type);
				types.put(annotation_type, type_info);
				multityped_result.add(type_info);
				add_default_type = false;
			}
		}
		if (add_default_type) {
			TypeInfo default_type_info = getDefaultTypeInfo(decl_type);
			Collection<TypeInfo> result = new ArrayList<TypeInfo>();
			result.add(default_type_info);
			return result;
		} else
			return multityped_result;
	}

	private static Map<ParameterDeclaration, Collection<TypeInfo>> getTypeInfoMap(TypeMap type_map, MethodDeclaration method) {
		Map<ParameterDeclaration, Collection<TypeInfo>> map = new HashMap<ParameterDeclaration, Collection<TypeInfo>>();
		for (ParameterDeclaration param : method.getParameters()) {
			Collection<TypeInfo> types = getTypeInfos(type_map, param, param.getType());
			map.put(param, types);
		}
		return map;
	}

	public static Collection<Map<ParameterDeclaration, TypeInfo>> getTypeInfoCrossProduct(TypeMap type_map, MethodDeclaration method) {
		Collection<ParameterDeclaration> parameter_collection = method.getParameters();
		ParameterDeclaration[] parameters = new ParameterDeclaration[parameter_collection.size()];
		parameter_collection.toArray(parameters);
		Collection<Map<ParameterDeclaration, TypeInfo>> cross_product = new ArrayList<Map<ParameterDeclaration, TypeInfo>>();
		getCrossProductRecursive(0, parameters, getTypeInfoMap(type_map, method),
				new HashMap<ParameterDeclaration, TypeInfo>(), cross_product);
		return cross_product;
	}

	private static void getCrossProductRecursive(int index, ParameterDeclaration[] parameters, Map<ParameterDeclaration,
			Collection<TypeInfo>> typeinfos_map, Map<ParameterDeclaration, TypeInfo> current_instance,
			Collection<Map<ParameterDeclaration, TypeInfo>> cross_product) {
		if (index == parameters.length) {
			cross_product.add(current_instance);
			return;
		}
		ParameterDeclaration param = parameters[index];
		Collection<TypeInfo> typeinfos = typeinfos_map.get(param);
		if (typeinfos != null) {
			for (TypeInfo typeinfo : typeinfos) {
				Map<ParameterDeclaration, TypeInfo> instance = new HashMap<ParameterDeclaration, TypeInfo>(current_instance);
				instance.put(param, typeinfo);
				getCrossProductRecursive(index + 1, parameters, typeinfos_map, instance, cross_product);
			}
		}
	}
}
