package com.vocacional.prestamoinso.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocacional.prestamoinso.Config.SupabaseConfig;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class SupabaseService {

    @Autowired
    private SupabaseConfig supabaseConfig;

    @Autowired
    private OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Realiza una consulta SELECT a una tabla de Supabase
     */
    public String select(String table, String select, String filter) throws IOException {
        String url = supabaseConfig.getRestApiUrl() + "/" + table;
        
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        
        if (select != null && !select.isEmpty()) {
            urlBuilder.addQueryParameter("select", select);
        }
        
        // Procesar filtros como parámetros de consulta directos
        if (filter != null && !filter.isEmpty()) {
            if (filter.startsWith("limit=")) {
                String limitValue = filter.substring(6);
                urlBuilder.addQueryParameter("limit", limitValue);
            } else if (filter.startsWith("order=")) {
                String orderValue = filter.substring(6);
                urlBuilder.addQueryParameter("order", orderValue);
            } else {
                // Parsear filtros múltiples separados por &
                String[] filterParts = filter.split("&");
                for (String filterPart : filterParts) {
                    if (filterPart.contains("=")) {
                        String[] keyValue = filterPart.split("=", 2);
                        if (keyValue.length == 2) {
                            urlBuilder.addQueryParameter(keyValue[0], keyValue[1]);
                        }
                    }
                }
            }
        }

        String finalUrl = urlBuilder.build().toString();
        System.out.println("URL construida: " + finalUrl);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("apikey", supabaseConfig.getAnonKey())
                .addHeader("Authorization", "Bearer " + supabaseConfig.getAnonKey())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        System.out.println("Headers enviados:");
        System.out.println("apikey: " + supabaseConfig.getAnonKey());
        System.out.println("Authorization: Bearer " + supabaseConfig.getAnonKey());

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println("Código de respuesta: " + response.code());
            System.out.println("Mensaje de respuesta: " + response.message());
            
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Sin cuerpo de respuesta";
                System.out.println("Cuerpo del error: " + errorBody);
                throw new IOException("Error en consulta: " + response.code() + " - " + response.message() + " - " + errorBody);
            }
            return response.body().string();
        }
    }

    /**
     * Inserta un nuevo registro en una tabla de Supabase
     */
    public String insert(String table, Object data) throws IOException {
        String url = supabaseConfig.getRestApiUrl() + "/" + table;
        
        String jsonData = objectMapper.writeValueAsString(data);
        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseConfig.getServiceRoleKey())
                .addHeader("Authorization", "Bearer " + supabaseConfig.getServiceRoleKey())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en inserción: " + response.code() + " - " + response.message());
            }
            return response.body().string();
        }
    }

    /**
     * Actualiza un registro en una tabla de Supabase
     */
    public String update(String table, Object data, String filter) throws IOException {
        String url = supabaseConfig.getRestApiUrl() + "/" + table + "?" + filter;
        
        String jsonData = objectMapper.writeValueAsString(data);
        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseConfig.getServiceRoleKey())
                .addHeader("Authorization", "Bearer " + supabaseConfig.getServiceRoleKey())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .patch(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en actualización: " + response.code() + " - " + response.message());
            }
            return response.body().string();
        }
    }

    /**
     * Elimina un registro de una tabla de Supabase
     */
    public String delete(String table, String filter) throws IOException {
        String url = supabaseConfig.getRestApiUrl() + "/" + table + "?" + filter;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseConfig.getServiceRoleKey())
                .addHeader("Authorization", "Bearer " + supabaseConfig.getServiceRoleKey())
                .addHeader("Content-Type", "application/json")
                .delete()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en eliminación: " + response.code() + " - " + response.message());
            }
            return response.body().string();
        }
    }

    /**
     * Convierte la respuesta JSON a una lista de objetos
     */
    public <T> List<T> parseJsonToList(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * Convierte la respuesta JSON a un objeto
     */
    public <T> T parseJsonToObject(String json, Class<T> clazz) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);
        if (jsonNode.isArray() && jsonNode.size() > 0) {
            return objectMapper.treeToValue(jsonNode.get(0), clazz);
        }
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Prueba la conexión con Supabase
     */
    public String testConnection() {
        System.out.println(">>> INICIANDO TEST CONNECTION <<<");
        try {
            System.out.println("=== CONFIGURACIÓN SUPABASE ===");
            System.out.println("Supabase URL: " + supabaseConfig.getSupabaseUrl());
            System.out.println("REST API URL: " + supabaseConfig.getRestApiUrl());
            System.out.println("Anon Key: " + supabaseConfig.getAnonKey());
            System.out.println("===============================");
            
            // Intentar hacer una consulta simple a la tabla users
            String result = select("users", "id", "limit=1");
            return "Conexión exitosa con Supabase. Resultado: " + result;
        } catch (Exception e) {
            System.out.println("Error al probar conexión con Supabase: " + e.getMessage());
            e.printStackTrace();
            return "Error al probar conexión con Supabase: " + e.getMessage();
        }
    }

    // ==================== MÉTODOS ESPECÍFICOS PARA REEMPLAZAR JPA REPOSITORIES ====================

    /**
     * Busca un registro por ID
     */
    public <T> T findById(String table, Long id, Class<T> clazz) throws IOException {
        String result = select(table, "*", "id=eq." + id);
        return parseJsonToObject(result, clazz);
    }

    /**
     * Busca todos los registros de una tabla
     */
    public <T> List<T> findAll(String table, Class<T> clazz) throws IOException {
        String result = select(table, "*", null);
        return parseJsonToList(result, clazz);
    }

    /**
     * Busca todos los registros ordenados por fecha de creación descendente
     */
    public <T> List<T> findAllOrderByFechaCreacionDesc(String table, Class<T> clazz) throws IOException {
        String result = select(table, "*", "order=fechaCreacion.desc");
        return parseJsonToList(result, clazz);
    }

    /**
     * Verifica si existe un registro con un campo específico
     */
    public boolean existsByField(String table, String field, String value) throws IOException {
        String result = select(table, "id", field + "=eq." + value + "&limit=1");
        JsonNode jsonNode = objectMapper.readTree(result);
        return jsonNode.isArray() && jsonNode.size() > 0;
    }

    /**
     * Busca un registro por un campo específico
     */
    public <T> T findByField(String table, String field, String value, Class<T> clazz) throws IOException {
        String result = select(table, "*", field + "=eq." + value);
        return parseJsonToObject(result, clazz);
    }

    /**
     * Busca registros por un campo específico (múltiples resultados)
     */
    public <T> List<T> findAllByField(String table, String field, String value, Class<T> clazz) throws IOException {
        String result = select(table, "*", field + "=eq." + value);
        return parseJsonToList(result, clazz);
    }

    /**
     * Busca registros por dos campos específicos
     */
    public <T> T findByTwoFields(String table, String field1, String value1, String field2, String value2, Class<T> clazz) throws IOException {
        String result = select(table, "*", field1 + "=eq." + value1 + "&" + field2 + "=eq." + value2);
        return parseJsonToObject(result, clazz);
    }

    /**
     * Busca registros por dos campos específicos (múltiples resultados)
     */
    public <T> List<T> findAllByTwoFields(String table, String field1, String value1, String field2, String value2, Class<T> clazz) throws IOException {
        String result = select(table, "*", field1 + "=eq." + value1 + "&" + field2 + "=eq." + value2);
        return parseJsonToList(result, clazz);
    }

    /**
     * Ejecuta una consulta personalizada con filtros complejos
     */
    public String executeCustomQuery(String table, String select, String filter) throws IOException {
        return select(table, select, filter);
    }

    /**
     * Actualiza un registro por ID
     */
    public <T> T updateById(String table, Long id, Object data, Class<T> clazz) throws IOException {
        String result = update(table, data, "id=eq." + id);
        return parseJsonToObject(result, clazz);
    }

    /**
     * Elimina un registro por ID
     */
    public void deleteById(String table, Long id) throws IOException {
        delete(table, "id=eq." + id);
    }

    /**
     * Elimina registros por un campo específico
     */
    public void deleteByField(String table, String field, String value) throws IOException {
        delete(table, field + "=eq." + value);
    }

    /**
     * Busca registros usando un filtro personalizado
     */
    public <T> List<T> findByFilter(String table, String filter, Class<T> clazz) throws IOException {
        String result = select(table, "*", filter);
        return parseJsonToList(result, clazz);
    }
}