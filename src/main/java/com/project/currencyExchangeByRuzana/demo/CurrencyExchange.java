package com.project.currencyExchangeByRuzana.demo;

import com.project.currencyExchangeByRuzana.demo.entities.Currency;
import com.project.currencyExchangeByRuzana.demo.repositories.CurrencyRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CurrencyExchange extends TelegramLongPollingBot {

    private Long chat_id = 0L;

    @Autowired
    CurrencyRepository currencyRepository;

    public double[] getDataFromPage(){
        try {
            Document doc = Jsoup.connect("https://prodengi.kz/kurs-valyut").get();  //take a page from the site
            Elements element = doc.select("div.national-bank-rate-simple-detail__item");  //take <div class = "national-bank-rate-simple-detail__item">
            String usd = element.get(0).children().get(1).text();       //take the first data of <div class = "national-bank-rate-simple-detail__item"> and take its second child, because children are USD and value
            String eur = element.get(1).children().get(1).text();       //take the second data and second child
            String rub = element.get(2).children().get(1).text();       //take the third data and second child
            double[] currencies = {Double.parseDouble(usd), Double.parseDouble(eur), Double.parseDouble(rub)};
            return currencies;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Writing data to the database every 10 minutes
    @Scheduled(fixedRate = 60*1000)
    public void addToDatabase(){
        double[] data = getDataFromPage();      //try to get data from the site by calling the getDataFromPage function
        Currency currency = new Currency(null, data[0], data[1], data[2], new Date());          //create new object
        currencyRepository.save(currency);      //save to database by using Hibernate
        System.out.println("            asdasdsadsadasda            ");
    }


    public double[] currencies(double amount){
        System.out.println("        " + currencyRepository.findTopByOrderByUpdatedAtDesc().getId());
        double[] data = getDataFromPage();      //try to get data from the site by calling the getDataFromPage function
        double[] exchangeAmount = {amount*data[0], amount*data[1], amount*data[2]};        //exchange amount into each currency
        return exchangeAmount;
    }

    //operations
    @Override
    public void onUpdateReceived(Update update) {
        //if there is message
        if(update.hasMessage()){
            //and it has text
            if(update.getMessage().hasText()){
                //we try to take input text
                try {
                    chat_id = update.getMessage().getChatId();
                    String text = update.getMessage().getText();
                    //if this is not command /start
                    if(!text.equals("/start")){
                        try {
                            double amount = Double.parseDouble(text);
                            if(amount>0){
                                execute(sendKey(amount, update.getMessage().getChatId()).setText("Выберите валюту, из которой хотите превести\nИли можете ввести новую сумму"));
                            }else{
                                execute(new SendMessage(update.getMessage().getChatId(), "Пожалуйста, введите число больше нуля")).getText();
                            }
                        } catch (NumberFormatException | NullPointerException nfe) {
                            execute(new SendMessage(update.getMessage().getChatId(), "Пожалуйста, введите число")).getText();
                        }
                    }else {
                        //if this is a command, we ask the user to enter the amount that they want to exchange
                        execute(new SendMessage(update.getMessage().getChatId(), "Здравствуйте! Введите сумму, которую вы хотите выразить в тенге")).getText();
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else if(update.hasCallbackQuery()){
            //If the button was pressed, then give an answer
            try{
                execute(new SendMessage().setText(update.getCallbackQuery().getData()).setChatId(update.getCallbackQuery().getMessage().getChatId()));
            }catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
    //Create the buttons
    public SendMessage sendKey(double amount, long chatId){
        double[] amounts = currencies(amount);          //Calling the function to find out the exchange rate
        //Creating a button field
        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();
        //Creating the first row
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        //Adding buttons with converted amounts
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("USD").setCallbackData(Double.toString(amounts[0])));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("EUR").setCallbackData(Double.toString(amounts[1])));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("RUB").setCallbackData(Double.toString(amounts[2])));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        return new SendMessage().setChatId(chatId).setReplyMarkup(inlineKeyboardMarkup);
    }

    @Override
    public String getBotUsername() {
        return "CurrencyExchangeRuzanaBot";
    }

    @Override
    public String getBotToken() {
        return "1420729813:AAFGIfsbScuYDw_805WbSwuTDe9yK824agQ";
    }
}
