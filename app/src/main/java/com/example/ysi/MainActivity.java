package com.example.ysi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements Serializable {
    //valikot Suomen sekä Viron posteille.
    Spinner spinnerFI;
    Spinner spinnerES;
    TextView information;
    EditText input;
    String date;
    // splista = smarpostien tiedot, locations = kaupungit.
    ArrayList<smartPost> spLista_FI = new ArrayList<>();
    ArrayList<String> locations_FI = new ArrayList<>();
    ArrayList<smartPost> spLista_ES = new ArrayList<>();
    ArrayList<String> locations_ES = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        spinnerFI = findViewById(R.id.spinner1);
        spinnerES = findViewById(R.id.spinner2);
        information = findViewById(R.id.info);
        input = findViewById(R.id.editText);
        //luodaan 2 eri arraylist:taa Suomi/Viro
        readXML(spLista_FI, "http://iseteenindus.smartpost.ee/api/?request=destinations&country=FI&type=APT");
        readXML(spLista_ES, "http://iseteenindus.smartpost.ee/api/?request=destinations&country=EE&type=APT");
        //luodaan 2 eri valikkoa Suomi/Viro
        makeSpinner(spLista_FI, locations_FI, "FI");
        makeSpinner(spLista_ES, locations_ES, "ES");

        input.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                date = s.toString();

            }
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void makeSpinner(ArrayList<smartPost> data, ArrayList<String> locations, String id) {
        for(int i = 0; i < data.size(); i++){
            String city = data.get(i).city;
            if(i == data.size()-1){

            }else if(data.get(i+1).city.equals(city)){

            }else{
                locations.add(city);
            }
        }
        Collections.sort(locations);
        if(id.equals("FI")) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFI.setAdapter(adapter1);
        }else{
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerES.setAdapter(adapter2);
        }
    }

    public void getSelectedCityFI(View v){
        String selectedCity = spinnerFI.getSelectedItem().toString();
        getDetails(selectedCity, spLista_FI);
    }
    public void getSelectedCityES(View v){
        String selectedCity = spinnerES.getSelectedItem().toString();
        getDetails(selectedCity, spLista_ES);
    }

    public void getDetails(String s, ArrayList<smartPost> data){
        String info = "";
        //if(date.contentEquals("1")) {
            for (int i = 0; i < data.size(); i++) {
                String city = data.get(i).city;
                if (i == data.size() - 1) {
                } else if (city.equals(s)) {
                    info = info + data.get(i).address + " " + data.get(i).hours + "\n";
                }
            }
       // }
        information.setText(info);
        information.setMovementMethod(new ScrollingMovementMethod());
    }

    public void readXML(ArrayList<smartPost> list, String url){
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(url);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element:" + doc.getDocumentElement().getNodeName());
            NodeList lista = doc.getElementsByTagName("item");

            for (int i = 0; i < lista.getLength(); i++) {
                Node node = lista.item(i);
                //System.out.println("Element is: " + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String city = element.getElementsByTagName("city").item(0).getTextContent();
                    String address = element.getElementsByTagName("address").item(0).getTextContent();
                    String hours = element.getElementsByTagName("availability").item(0).getTextContent();
                    smartPost alkio = new smartPost();
                    alkio.city = city;
                    alkio.address = address;
                    alkio.hours = hours;
                    list.add(alkio);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }
    class smartPost{
        String address;
        String city;
        String hours;

        @NonNull
        @Override
        public String toString() {
            return city;
        }
        public String getAddress() {
            return address;
        }
        public String getCity() {
            return city;
        }
    }

}