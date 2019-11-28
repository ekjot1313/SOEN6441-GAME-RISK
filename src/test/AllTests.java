package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.GameEngine.GameEngineTestSuite;
import test.builder.BuilderTestSuite;
import test.daotest.DaoTestSuite;
import test.gametest.GameTestSuite;
import test.mapworkstest.MapWorksTestSuite;
import test.pattern.Strategy.StrategyTestSuite;
import test.viewtest.ViewTestSuite;

@RunWith(Suite.class)
@SuiteClasses({DaoTestSuite.class, MapWorksTestSuite.class, GameTestSuite.class, ViewTestSuite.class ,StrategyTestSuite.class , GameEngineTestSuite.class, BuilderTestSuite.class})
public class AllTests {
}