package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;
import br.com.codenation.model.Jogador;
import br.com.codenation.model.Time;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class DesafioMeuTimeApplication implements MeuTimeInterface {

	public Map<Long, Time> times = new HashMap<>();
	public Map<Long, Jogador> jogadores = new HashMap<>();

	@Desafio("incluirTime")
	public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal, String corUniformeSecundario) {

		if(times.containsKey(id))
			throw new IdentificadorUtilizadoException();

		times.put(id, new Time(id, nome, dataCriacao, corUniformePrincipal, corUniformeSecundario));
	}

	@Desafio("incluirJogador")
	public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade, BigDecimal salario) {

		verificaTimeExiste(idTime);

		if(jogadores.containsKey(id))
			throw new IdentificadorUtilizadoException();

		jogadores.put(id, new Jogador(id, idTime, nome, dataNascimento, nivelHabilidade, salario));

	}

	@Desafio("definirCapitao")
	public void definirCapitao(Long idJogador) {
		verificaJogadorExiste(idJogador);
		times.get(jogadores.get(idJogador).getIdTime()).setCapitaoId(idJogador);
	}

	@Desafio("buscarCapitaoDoTime")
	public Long buscarCapitaoDoTime(Long idTime) {
		verificaTimeExiste(idTime);

		if(times.get(idTime).getCapitaoId() == null)
			throw new CapitaoNaoInformadoException();

		return times.get(idTime).getCapitaoId();

	}

	@Desafio("buscarNomeJogador")
	public String buscarNomeJogador(Long idJogador) {
		verificaJogadorExiste(idJogador);
		return jogadores.get(idJogador).getNome();
	}

	@Desafio("buscarNomeTime")
	public String buscarNomeTime(Long idTime) {
		verificaTimeExiste(idTime);
		return times.get(idTime).getNome();
	}

	@Desafio("buscarJogadoresDoTime")
	public List<Long> buscarJogadoresDoTime(Long idTime) {

		verificaTimeExiste(idTime);
		return jogadores.values().stream()
				.filter(jogador -> jogador.getIdTime().intValue() == idTime.intValue())
				.map(Jogador::getId)
				.sorted()
				.collect(toList());

	}

	@Desafio("buscarMelhorJogadorDoTime")
	public Long buscarMelhorJogadorDoTime(Long idTime) {

		verificaTimeExiste(idTime);
		return jogadores.values().stream()
				.filter(jogador -> jogador.getIdTime().intValue() == idTime.intValue())
				.sorted(Comparator.comparingInt(Jogador::getNivelHabilidade).reversed().thenComparing(Jogador::getId))
				.map(Jogador::getId)
				.findFirst()
				.get();

	}

	@Desafio("buscarJogadorMaisVelho")
	public Long buscarJogadorMaisVelho(Long idTime) {

		verificaTimeExiste(idTime);
		return jogadores.values().stream()
				.filter(jogador -> jogador.getIdTime().intValue() == idTime.intValue())
				.sorted(Comparator.comparing(Jogador::getDataNascimento).thenComparing(Jogador::getId))
				.map(Jogador::getId)
				.findFirst()
				.get();

	}

	@Desafio("buscarTimes")
	public List<Long> buscarTimes() {
		return ofNullable(times).orElse(emptyMap())
				.values()
				.stream()
				.map(Time::getId)
				.sorted()
				.collect(toList());
	}

	@Desafio("buscarJogadorMaiorSalario")
	public Long buscarJogadorMaiorSalario(Long idTime) {

		verificaTimeExiste(idTime);

		return jogadores.values().stream()
				.filter(jogador -> jogador.getIdTime().intValue() == idTime.intValue())
				.sorted(Comparator.comparing(Jogador::getSalario).reversed().thenComparing(Jogador::getId))
				.map(Jogador::getId)
				.findFirst()
				.orElse(null);

	}

	@Desafio("buscarSalarioDoJogador")
	public BigDecimal buscarSalarioDoJogador(Long idJogador) {

		verificaJogadorExiste(idJogador);
		return jogadores.get(idJogador).getSalario();
	}

	@Desafio("buscarTopJogadores")
	public List<Long> buscarTopJogadores(Integer top) {

		return ofNullable(jogadores)
				.map(Map::values)
				.orElse(emptyList())
				.stream()
				.sorted(Comparator.comparing(Jogador::getNivelHabilidade).reversed().thenComparing(Jogador::getId))
				.limit(top)
				.map(Jogador::getId)
				.collect(toList());
	}

	@Desafio("buscarCorCamisaTimeDeFora")
	public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {

		verificaTimeExiste(timeDaCasa);
		verificaTimeExiste(timeDeFora);

		return times.get(timeDaCasa).getCorUniformePrincipal()
				.equals(times.get(timeDeFora).getCorUniformePrincipal())
				? times.get(timeDeFora).getCorUniformeSecundario()
				: times.get(timeDeFora).getCorUniformePrincipal();

	}

	public void verificaTimeExiste(Long idTime){
		if(!times.containsKey(idTime)){
			throw new TimeNaoEncontradoException();
		}
	}
	public void verificaJogadorExiste(Long idJogador){
		if(!jogadores.containsKey(idJogador))
			throw new JogadorNaoEncontradoException();

	}


}
