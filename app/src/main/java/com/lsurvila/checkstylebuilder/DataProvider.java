package com.lsurvila.checkstylebuilder;

import android.os.AsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс, занимающийся поставкой данных (JSON и картинок)
 * @author RareScrap
 */
public class DataProvider {
    /** Вызывается, когда данные скачаны и распарсены */
    public interface DataReady {
        /*public*/ void onDataReady();
        void onDownloadError();
    }

    /** Адрес, откуда будет скачан JSON с данными */
    public URL jsonURL;
    /** Хранилище для загруженных данных в формате JSON */
    public JSONObject downloadedJSON = null;


    /** Объект реализации интерфейса. Приходит из вне */
    public DataReady dataReady;

    /**
     * Конструктор, инициализирующий свои поля
     * @param dataReady Реализация интерфейса, метод которого вызывается, когда данные распарсены
     *                  и готовы к работе
     * @param url Адрес, откуда будет скачан JSON с данными
     */
    public DataProvider(DataReady dataReady, URL url) {
        this.dataReady = dataReady;
        this.jsonURL = url;
    }

    /**
     * Запускает загрузку данных в виде JSON
     */
    public void startDownloadData() {
        // Запрос на получение данных
        try {
            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute(jsonURL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Обращение к REST-совместимому (якобы) веб-сервису за данными блюд и меню
    и сохранение этих данных в локальном файле HTML */
    /**
     * Внутренний класс {@link AsyncTask} для загрузки данных
     * в формате JSON.
     * @author RareScrap
     */
    private class GetDataTask extends AsyncTask<URL, Void, JSONObject> {
        /** Максимальное время ожидания данных */
        public int CONNECTION_TIMEOUT = 5000;

        /**
         * Получение данных из сети
         * @param params URL для получения JSON файла
         * @return JSON файл с категориями меню и блюдами в них
         */
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection(); // Для выдачи запроса достаточно открыть объект подключения
                connection.setConnectTimeout(this.CONNECTION_TIMEOUT);
                int response = connection.getResponseCode(); // Получить код ответа от веб-сервера

                //response = 404; // Это тест при недоступности сети

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();


                    return new JSONObject(builder.toString());
                }else {} // TODO: Реализовать поведение при недоступности сети
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // Закрыть HttpURLConnection
            }

            return null;
        }

        /**
         * Обработка ответа JSON и обновление ListView/GridView.
         * @param jsonObject JSON файл полученный после завершения работы doInBackground()
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                downloadedJSON = jsonObject; // Сохранение загруженного файла

                parseJSON(jsonObject); // Заполнение weatherList
                //menuItemArrayAdapter.notifyDataSetChanged(); // Связать с ListView

                // Прокрутить до верха
                /*if (currentMode == CARD_MODE) {
                    menuItemListListView.smoothScrollToPosition(0);
                }else { // currentMode == PLATE_MODE
                    menuItemListGridView.smoothScrollToPosition(0);
                }*/
            } else { // Информировать в случае, если данные не дошли
                dataReady.onDownloadError();
            }
        }
    }

    /**
     *
     * @param jsonObject Входящий JSON файл
     */
    private void parseJSON(JSONObject jsonObject) {
        // Стирание старых данных
        // Инормировать, что данные готовы к использованию
        dataReady.onDataReady();
    }
}
