package ru.mirea.web3.web3javaclient.entity;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Post {
    private Integer id;
    @NotEmpty
    private String description;
    @NotNull
    @Size(min = 3, max = 100)
    private byte[] picture;
    private String userToken;
    private Date date;
}
