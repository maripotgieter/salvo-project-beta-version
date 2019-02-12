package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
		System.out.println("Hello Mari");

	}

		@Bean
		public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
			return (args) -> {


				Player player1 = new Player("j.bauer@ctu.gov", "24");
				Player player2 = new Player("c.obrian@ctu.gov", "42");
				Player player3 = new Player("kim_bauer@gmail.com", "kb");
				Player player4 = new Player("t.almeida@ctu.gov", "mole");
				playerRepository.save(player1);
				playerRepository.save(player2);
				playerRepository.save(player3);
				playerRepository.save(player4);

				Date date1 = new Date();
				Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
				Date date3 = Date.from(date2.toInstant().plusSeconds(3600));

				Game game1 = new Game(date1);
				Game game2 = new Game(date2);
				Game game3 = new Game(date3);
				Game game4 = new Game(date1);
				Game game5 = new Game(date2);
				Game game6 = new Game(date3);
				Game game7 = new Game(date1);
				Game game8 = new Game(date2);

				gameRepository.save(game1);
				gameRepository.save(game2);
				gameRepository.save(game3);
				gameRepository.save(game4);
				gameRepository.save(game5);
				gameRepository.save(game6);
				gameRepository.save(game7);
				gameRepository.save(game8);

				GamePlayer gamePlayer1 = new GamePlayer(date1,game1,player1);
				GamePlayer gamePlayer2 = new GamePlayer(date1, game1, player2);
				GamePlayer gamePlayer3 = new GamePlayer(date2,game2, player1);
				GamePlayer gamePlayer4 = new GamePlayer(date2, game2, player2);
				GamePlayer gamePlayer5 = new GamePlayer(date3, game3, player2);
				GamePlayer gamePlayer6 = new GamePlayer(date3, game3, player4);
				GamePlayer gamePlayer7 = new GamePlayer(date1, game4, player2);
				GamePlayer gamePlayer8 = new GamePlayer(date1, game4, player1);
				GamePlayer gamePlayer9 = new GamePlayer(date1, game5, player4);
				GamePlayer gamePlayer10 = new GamePlayer(date1, game5, player1);
				GamePlayer gamePlayer11 = new GamePlayer(date1, game6, player3);
				GamePlayer gamePlayer12 = new GamePlayer(date1, game7, player4);
				GamePlayer gamePlayer13 = new GamePlayer(date1, game8, player3);
				GamePlayer gamePlayer14 = new GamePlayer(date1, game8, player4);

				gamePlayerRepository.save(gamePlayer1);
				gamePlayerRepository.save(gamePlayer2);
				gamePlayerRepository.save(gamePlayer3);
				gamePlayerRepository.save(gamePlayer4);
				gamePlayerRepository.save(gamePlayer5);
				gamePlayerRepository.save(gamePlayer6);
				gamePlayerRepository.save(gamePlayer7);
				gamePlayerRepository.save(gamePlayer8);
				gamePlayerRepository.save(gamePlayer9);
				gamePlayerRepository.save(gamePlayer10);
				gamePlayerRepository.save(gamePlayer11);
				gamePlayerRepository.save(gamePlayer12);
				gamePlayerRepository.save(gamePlayer13);
				gamePlayerRepository.save(gamePlayer14);

				List<String> location1 = Arrays.asList("H2","H3","H4");
				List<String>location2 = Arrays.asList("E1","F1","G1");
				List<String>location3 = Arrays.asList("B4", "B5");
				List<String>location4 = Arrays.asList("B5","C5","D5");
				List<String>location5 = Arrays.asList("F1", "F2");
				List<String>location6 = Arrays.asList("C6", "C7");
				List<String>location7 = Arrays.asList("A2", "A3", "A4");
				List<String>location8 = Arrays.asList("G6", "H6");

				Ship ship1 = new Ship("Raccoon", gamePlayer1, location1);
				Ship ship2 = new Ship("Fox", gamePlayer1, location2);
				Ship ship3 = new Ship("Hedgehog", gamePlayer1, location3);
				Ship ship4 = new Ship("Raccoon", gamePlayer2, location4);
				Ship ship5 = new Ship("Hedgehog", gamePlayer2, location5);

				Ship ship6 = new Ship("Fox", gamePlayer3, location4);
				Ship ship7 = new Ship("Hedgehog", gamePlayer3, location6);
				Ship ship8 = new Ship("Raccoon", gamePlayer4, location7);
				Ship ship9 = new Ship("Hedgehog", gamePlayer4, location8);

				Ship ship10 = new Ship("Hedgehog", gamePlayer5, location3);
				Ship ship11 = new Ship("Raccoon", gamePlayer5, location7);
				Ship ship12 = new Ship("Fox", gamePlayer6, location7);
				Ship ship13 = new Ship("Hedgehog", gamePlayer6, location8);

				Ship ship14 = new Ship("Raccoon", gamePlayer7, location4);
				Ship ship15 = new Ship("Hedgehog", gamePlayer7, location6);
				Ship ship16 = new Ship("Fox", gamePlayer8, location7);
				Ship ship17 = new Ship("Hedgehog", gamePlayer8, location8);

				Ship ship18 = new Ship("Raccoon", gamePlayer9, location4);
				Ship ship19 = new Ship("Hedgehog", gamePlayer9, location6);
				Ship ship20 = new Ship("Fox", gamePlayer10, location7);
				Ship ship21 = new Ship("Hedgehog", gamePlayer10, location8);

				Ship ship22 = new Ship("Raccoon", gamePlayer11, location4);
				Ship ship23 = new Ship("Hedgehog", gamePlayer11, location6);

				Ship ship24 = new Ship("Raccoon", gamePlayer13, location4);
				Ship ship25 = new Ship("Hedgehog", gamePlayer13, location6);
				Ship ship26 = new Ship("Fox", gamePlayer14, location7);
				Ship ship27 = new Ship("Hedgehog", gamePlayer14, location8);

				shipRepository.save(ship1);
				shipRepository.save(ship2);
				shipRepository.save(ship3);
				shipRepository.save(ship4);
				shipRepository.save(ship5);
				shipRepository.save(ship6);
				shipRepository.save(ship7);
				shipRepository.save(ship8);
				shipRepository.save(ship9);
				shipRepository.save(ship10);
				shipRepository.save(ship11);
				shipRepository.save(ship12);
				shipRepository.save(ship13);
				shipRepository.save(ship14);
				shipRepository.save(ship15);
				shipRepository.save(ship16);
				shipRepository.save(ship17);
				shipRepository.save(ship18);
				shipRepository.save(ship19);
				shipRepository.save(ship20);
				shipRepository.save(ship21);
				shipRepository.save(ship22);
				shipRepository.save(ship23);
				shipRepository.save(ship24);
				shipRepository.save(ship25);
				shipRepository.save(ship26);
				shipRepository.save(ship27);

				List<String>salvoLocation1 =  Arrays.asList("B5", "C5", "F1");
				List<String>salvoLocation2 =  Arrays.asList("B4", "B5", "B6");
				List<String>salvoLocation3 =  Arrays.asList("F2", "D5");
				List<String>salvoLocation4 =  Arrays.asList("E1", "H3", "A2");
				List<String>salvoLocation5 =  Arrays.asList("A2", "A4", "G6");
				List<String>salvoLocation6 =  Arrays.asList("B5", "D5", "C7");
				List<String>salvoLocation7 =  Arrays.asList("A3", "H6");
				List<String>salvoLocation8 =  Arrays.asList("C5", "C6");

				List<String>salvoLocation9 =  Arrays.asList("G6", "H6", "A4");
				List<String>salvoLocation10 =  Arrays.asList("H1", "H2", "H3");
				List<String>salvoLocation11 =  Arrays.asList("A2", "A3", "D8");
				List<String>salvoLocation12 =  Arrays.asList("E1", "F2", "G3");

				List<String>salvoLocation13 =  Arrays.asList("A3", "A4", "F7");
				List<String>salvoLocation14 =  Arrays.asList("B5", "C6", "H1");
				List<String>salvoLocation15 =  Arrays.asList("A2", "G6", "H6");
				List<String>salvoLocation16 =  Arrays.asList("C5", "C7", "D5");

				List<String>salvoLocation17 =  Arrays.asList("A1", "A2", "A3");
				List<String>salvoLocation18 =  Arrays.asList("B5", "B6", "C7");
				List<String>salvoLocation19 =  Arrays.asList("G6", "G7", "G8");
				List<String>salvoLocation20 =  Arrays.asList("C6", "D6", "E6");

				List<String>salvoLocation21 =  Arrays.asList("H1", "H8");


				Salvo salvo1 = new Salvo(1, gamePlayer1, salvoLocation1);
				Salvo salvo2 = new Salvo(1, gamePlayer2, salvoLocation2);
				Salvo salvo3 = new Salvo (2, gamePlayer1, salvoLocation3);
				Salvo salvo4 = new Salvo (2, gamePlayer2, salvoLocation4);
				Salvo salvo5 = new Salvo (3, gamePlayer1, salvoLocation5);
				Salvo salvo6 = new Salvo (3, gamePlayer2, salvoLocation6);
				Salvo salvo7 = new Salvo (2, gamePlayer3, salvoLocation7);
				Salvo salvo8 = new Salvo (2, gamePlayer4, salvoLocation8);

				Salvo salvo9 = new Salvo (1, gamePlayer5, salvoLocation9);
				Salvo salvo10 = new Salvo (1, gamePlayer6, salvoLocation10);
				Salvo salvo11 = new Salvo (2, gamePlayer5, salvoLocation11);
				Salvo salvo12 = new Salvo (2, gamePlayer6, salvoLocation12);

				Salvo salvo13 = new Salvo (1, gamePlayer7, salvoLocation13);
				Salvo salvo14 = new Salvo (1, gamePlayer8, salvoLocation14);
				Salvo salvo15 = new Salvo (2, gamePlayer7, salvoLocation15);
				Salvo salvo16 = new Salvo (2, gamePlayer8, salvoLocation16);

				Salvo salvo17 = new Salvo (1, gamePlayer9, salvoLocation17);
				Salvo salvo18 = new Salvo (1, gamePlayer10, salvoLocation18);
				Salvo salvo19 = new Salvo (2, gamePlayer9, salvoLocation19);
				Salvo salvo20 = new Salvo (2, gamePlayer10, salvoLocation20);

				Salvo salvo21 = new Salvo (3, gamePlayer10, salvoLocation21);


				salvoRepository.save(salvo1);
				salvoRepository.save(salvo2);
				salvoRepository.save(salvo3);
				salvoRepository.save(salvo4);
				salvoRepository.save(salvo5);
				salvoRepository.save(salvo6);
				salvoRepository.save(salvo7);
				salvoRepository.save(salvo8);
				salvoRepository.save(salvo9);
				salvoRepository.save(salvo10);
				salvoRepository.save(salvo11);
				salvoRepository.save(salvo12);
				salvoRepository.save(salvo13);
				salvoRepository.save(salvo14);
				salvoRepository.save(salvo15);
				salvoRepository.save(salvo16);
				salvoRepository.save(salvo17);
				salvoRepository.save(salvo18);
				salvoRepository.save(salvo19);
				salvoRepository.save(salvo20);
				salvoRepository.save(salvo21);

				Date finishDate1 = Date.from(date1.toInstant().plusSeconds(1800));
				Date finishDate2 = Date.from(date1.toInstant().plusSeconds(5400));
				Date finishDate3 = Date.from(date1.toInstant().plusSeconds(9000));

				Score score1 = new Score(player1, game1, 1.0, finishDate1);
				Score score2 = new Score(player2, game1, 0.0, finishDate1);
				Score score3 = new Score(player1, game2, 0.5, finishDate2);
				Score score4 = new Score(player2, game2, 0.5, finishDate2);
				Score score5 = new Score(player2, game3, 1.0, finishDate3);
				Score score6 = new Score(player4, game3, 0.0, finishDate3);
				Score score7 = new Score(player2, game4, 0.5, finishDate1);
				Score score8 = new Score(player1, game4, 0.5, finishDate1);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);
            scoreRepository.save(score7);
            scoreRepository.save(score8);


			};
		}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				.antMatchers("/web/games.html").permitAll()
				.antMatchers("/web/games.js").permitAll()
				.antMatchers("/web/style.css").permitAll()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/api/scoreboard").permitAll()
				.antMatchers("/web/images/**").permitAll()
				.antMatchers("/rest").denyAll()
				.anyRequest().fullyAuthenticated()
				.and()
				.formLogin();

		http.formLogin()
				.usernameParameter("userName")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}

	}


}
