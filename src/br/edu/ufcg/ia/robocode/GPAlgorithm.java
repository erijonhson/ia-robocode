package br.edu.ufcg.ia.robocode;

import org.jgap.*;
import org.jgap.impl.*;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * It will run several battles in Robocode and make use of Genetic Algorithm to train our robot.
 */
public class GPAlgorithm extends FitnessFunction {

	private static final int THREAD_SIZE = 4;
	private ExecutorService workers = Executors.newFixedThreadPool(THREAD_SIZE);

	private Configuration conf;

	private SpinnerNumberModel model = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);

	private static final int POPULATION_SIZE = 100;
	private static final int MAX_GENERATIONS = 45;
	public static int robotScore, enemyScore;

	public static void main(String[] args) throws Exception {
		System.out.println("Training begin");
		new GPAlgorithm().runAlgorithm();
	}

	public void runAlgorithm() throws Exception {
		conf = new DefaultConfiguration();
		BestChromosomesSelector best = new BestChromosomesSelector(conf, 0.6);

		best.setDoubletteChromosomesAllowed(false);

		conf.addNaturalSelector(best, false);
		conf.addGeneticOperator(new CrossoverOperator(conf, 0.35));
		final int mutationRate = (Integer) model.getValue();
		conf.addGeneticOperator(new MutationOperator(conf, mutationRate));

		conf.setPreservFittestIndividual(false);
		conf.setFitnessFunction(this);

		Gene[] genes = getGenes();
		IChromosome primeiroCromossomo = new Chromosome(conf, genes);
		conf.setSampleChromosome(primeiroCromossomo);
		conf.setPopulationSize(POPULATION_SIZE);

		Genotype population = Genotype.randomInitialGenotype(conf);

		List<Integer> hackLoop = new LinkedList<>();
		for (int i = 1; i <= MAX_GENERATIONS; ++i) {
			hackLoop.add(i);
		}

		hackLoop.forEach(index -> workers.execute(() -> {
			population.evolve();
			IChromosome bestFit = population.getFittestChromosome();
			System.out.printf("--- Best solution after %d generation: %s  ---\n", index, bestFit);
			saveInfoAboutBestFit(index, bestFit.getFitnessValue());
			if (index == MAX_GENERATIONS) {
				createRobot(bestFit);
			}
		}));
	}

	private synchronized void saveInfoAboutBestFit(int i, double fitnessValue) {
		FileWriter fw = null;
		try {
			fw = new FileWriter("results/data.csv", true);
			StringBuilder sb = new StringBuilder();
			sb.append(i).append(";").append(fitnessValue).append("\n");
			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Gene[] getGenes() throws InvalidConfigurationException {
		Gene[] sample = new Gene[RobotFactory.MAX_ROBOT_ID];
		for (int i = 0; i < RobotFactory.MAX_ROBOT_ID; i++) {
			sample[i] = new IntegerGene(conf, RobotFactory.MIN_ROBOT_ID, RobotFactory.MAX_ROBOT_ID);
		}
		return sample;
	}

	/**
	 * Creates a robot from chromosome
	 */
	private void createRobot(IChromosome chromo) {
		int[] robotConfig = new int[RobotFactory.MAX_ROBOT_ID];
		Gene[] genes = chromo.getGenes();
		for (int i = 0; i < RobotFactory.MAX_ROBOT_ID; i++) {
			robotConfig[i] = (int) genes[i].getAllele();
		}
		RobotFactory.create(robotConfig);
	}

	public static void updateScores(String name, int score) {
		if (name.equals(RobotFactory.MODEL_NAME)) {
			robotScore = score;
		} else {
			enemyScore = score;
		}
	}

	@Override
	protected double evaluate(IChromosome chromosome) {
		int numberOfRounds = 2;
		int fitness = 0;

		createRobot(chromosome);

		RobocodeEngine engine = new RobocodeEngine();
		engine.addBattleListener(new BattleObserver());
		engine.setVisible(false); // change to true to view batle on the screen

		BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600);
		RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.Crazy, " + RobotFactory.MODEL_NAME);
		BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);
		engine.runBattle(battleSpec, true);
		fitness += robotScore;

		battlefield = new BattlefieldSpecification(800, 600);
		selectedRobots = engine.getLocalRepository("sample.Walls, " + RobotFactory.MODEL_NAME);
		battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);
		engine.runBattle(battleSpec, true);

		fitness += robotScore;

		engine.close();

		return fitness / 2;
	}

}
