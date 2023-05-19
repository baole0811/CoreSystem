package com.example.backendapi.ModelMapping;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeBook {
    //info bookRecipient
    private String userNameBookRecipient;
    private String emailBookRecipient;
    private String phoneNumberBookRecipient;
    //info bookGiver

    private String nameBook;
    private String userNameBookGiver;
    private String emailBookGiver;
}
