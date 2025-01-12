package de.leifker.scope;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;

import de.leifker.dto.Infos;

public class InfoTaskScope extends StructuredTaskScope<Infos> {

	  private final Collection<Infos> results = new ConcurrentLinkedQueue<>();
	  private final Collection<Throwable> exception = new ConcurrentLinkedQueue<>();

	  @Override
	  protected void handleComplete(Subtask<? extends Infos> subtask) {
	    if (subtask.state() == Subtask.State.FAILED) {
	      exception.add(subtask.exception());
	    } else if (subtask.state() == Subtask.State.SUCCESS) {
	      results.add(subtask.get());
	    }
	  }

	  public Infos bestInfos() {
	    if (this.results.isEmpty()) {
	      var rte = new RuntimeException("Keine Daten vorhanden");

	      exception.forEach(rte::addSuppressed);
	      throw rte;
	    }
	    return results.stream()
	        .max(Comparator.comparingInt(infos -> infos.info().size()))
	        .orElseThrow();
	  }
	}
