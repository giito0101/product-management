package com.giitotech.product_management;

import com.giitotech.product_management.entity.Role;
import com.giitotech.product_management.entity.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

//指定したパッケージのクラスを取り込みたい
@SpringBootApplication
// 指定したパッケージ以外のクラスを除外したい
//@ComponentScan(basePackages = { "com.giitotech.product_management" })
//@EntityScan(basePackages = "com.giitotech.product_management")
//@MapperScan("com.giitotech.product_management.mapper")
public class SpringWebApplication {
	
//    @Autowired
//    private ProductRepository productRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringWebApplication.class, args);
	}

}
