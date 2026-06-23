package com.example.extratohistoricotransacoes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.extratohistoricotransacoes.queries.dto.ExtratoDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ExtratoDto> extratoRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ExtratoDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 1. Configura a chave como String limpa
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 2. Configura o ObjectMapper para dar suporte a Records e Datas
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Suporte a LocalDate/LocalDateTime

        // 🌟 ESSENCIAL: Impede que o Jackson estoure erro ao ler o campo "@class" residual
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 3. Cria o serializador TIPADO apontando para o seu DTO e injeta o
        // objectMapper
        Jackson2JsonRedisSerializer<ExtratoDto> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
                ExtratoDto.class);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setDefaultSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
