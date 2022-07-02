package com.Anix.IO;

/**
 * Not used :)
 * But can be useful so I'm keeping it
 */
public class JacksonUtilsImpl {
	/*class MyNullKeySerializer extends JsonSerializer<Object> {
		@Override
		public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused) 
				throws IOException, JsonProcessingException {
			jsonGenerator.writeFieldName("");
		}
	}
	
	@SuppressWarnings("deprecation")
	protected ObjectMapper getObjectMapperForSerialization() {
		ObjectMapper mapper = new ObjectMapper();

		StdTypeResolverBuilder typeResolverBuilder = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
		typeResolverBuilder = typeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
		typeResolverBuilder.init(JsonTypeInfo.Id.CLASS, new ClassNameIdResolver(SimpleType.construct(Base.class), TypeFactory.defaultInstance()));
		mapper.setDefaultTyping(typeResolverBuilder);
		
		mapper.setVisibility(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		//mapper.disable(MapperFeature.USE_ANNOTATIONS);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY);
		
		mapper.getSerializerProvider().setNullKeySerializer(new MyNullKeySerializer());
		mapper.getSerializerProvider().setNullValueSerializer(new MyNullKeySerializer());
		
		
		
		return mapper;
	}
	
	@SuppressWarnings("deprecation")
	protected ObjectMapper getObjectMapperForDeserialization() {
		ObjectMapper mapper = new ObjectMapper();
		
		StdTypeResolverBuilder typeResolverBuilder = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
		typeResolverBuilder = typeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
		typeResolverBuilder.init(JsonTypeInfo.Id.CLASS, new ClassNameIdResolver(SimpleType.construct(Base.class), TypeFactory.defaultInstance()));
		mapper.setDefaultTyping(typeResolverBuilder);
		
		//mapper.setVisibilityChecker(
				//mapper.getSerializationConfig()
				//.getDefaultVisibilityChecker()
				///.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				//.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				//);
		mapper.setVisibility(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		//mapper.disable(MapperFeature.USE_ANNOTATIONS);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY);
		
		mapper.addHandler(new DeserializationProblemHandler() {
			@Override
			public Object handleMissingInstantiator(DeserializationContext ctxt, Class<?> instClass, ValueInstantiator valueInsta, JsonParser p, String msg) throws IOException {
				return super.handleMissingInstantiator(ctxt, instClass, valueInsta, p, msg);
			}
		});
		
		//mapper.addMixIn(Method.class, MethodMixIn.class);

		return mapper;
	}

	public String marshallIntoString(Collection<Behaviour> data) throws IOException {
		try {
			return getObjectMapperForSerialization().writeValueAsString(data);
		} catch(StackOverflowError e) {
			System.err.println("Stackoverflow bruh");
			
			return null;
		}
	}
	
	public <T> Collection<? extends T> unmarshall(String json, TypeReference<?> typeReference) throws IOException {
		return getObjectMapperForDeserialization().readValue(json, typeReference);
	}*/
}
