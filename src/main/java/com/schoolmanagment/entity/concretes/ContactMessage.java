package com.schoolmanagment.entity.concretes;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
//Getter Setter ToString Equals HashCode i otomatik getiriyor,Ayrica
//2 class i karsilastirmak icin equal gibi methodlarida getirir.
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
//Yeni nesne olusturmak yerine kopyasini olusturarak degistirmemizi sagliyor
//(Objede az degisiklik yapacagimida etkili)
public class ContactMessage implements Serializable {
//Entity classlarimizi genelde Serializable olarak kullaniriz.
/*

Serializable arayüzünün burada kullanılma nedeni, sınıfın nesnelerinin "serileştirilebilir" hale getirilmesidir.
Serializable, bir nesnenin durumunun bayt dizisine dönüştürülmesini sağlar,
böylece bu nesne bir ağ üzerinden iletilebilir, bir dosyaya yazılabilir veya hafızada saklanabilir.
Özellikle, verinin bir uygulama katmanından diğerine taşınması gerektiğinde bu işlem faydalıdır.
 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String subject;

    @NotNull
    private String message;

    //Gun onemli ona gore ayarlicaz.
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

}