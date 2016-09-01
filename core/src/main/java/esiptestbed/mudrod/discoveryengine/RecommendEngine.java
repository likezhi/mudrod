package esiptestbed.mudrod.discoveryengine;

import java.util.Properties;

import esiptestbed.mudrod.driver.ESDriver;
import esiptestbed.mudrod.driver.SparkDriver;
import esiptestbed.mudrod.recommendation.pre.ApiHarvester;
import esiptestbed.mudrod.recommendation.pre.OHCodeMatrixGenerator;
import esiptestbed.mudrod.recommendation.pre.OHEncodeMetadata;
import esiptestbed.mudrod.recommendation.pre.SessionCooccurenceMatrix;
import esiptestbed.mudrod.recommendation.pre.TFIDFGenerator;
import esiptestbed.mudrod.recommendation.pre.TranformMetadata;
import esiptestbed.mudrod.recommendation.process.ContentBasedCF;
import esiptestbed.mudrod.recommendation.process.TopicBasedCF;
import esiptestbed.mudrod.recommendation.process.sessionBasedCF;

public class RecommendEngine extends DiscoveryEngineAbstract {

  public RecommendEngine(Properties props, ESDriver es, SparkDriver spark) {
    super(props, es, spark);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void preprocess() {
    // TODO Auto-generated method stub
    System.out.println(
        "*****************Recommendation preprocessing starts******************");
    startTime = System.currentTimeMillis();

    DiscoveryStepAbstract harvester = new ApiHarvester(this.props, this.es,
        this.spark);
    harvester.execute();
    
    DiscoveryStepAbstract transformer = new TranformMetadata(this.props,
        this.es, this.spark);
    transformer.execute();

    DiscoveryStepAbstract obencoder = new OHEncodeMetadata(this.props, this.es,
        this.spark);
    obencoder.execute();

    DiscoveryStepAbstract matrixGen = new OHCodeMatrixGenerator(this.props,
        this.es, this.spark);
    matrixGen.execute();

    DiscoveryStepAbstract sessionMatrixGen = new SessionCooccurenceMatrix(
        this.props, this.es, this.spark);
    sessionMatrixGen.execute();
    
    DiscoveryStepAbstract topic = new TFIDFGenerator(this.props, this.es,
        this.spark);
    topic.execute();

    endTime = System.currentTimeMillis();
    System.out.println(
        "*****************Recommendation preprocessing ends******************Took "
            + (endTime - startTime) / 1000);
  }

  @Override
  public void process() {
    // TODO Auto-generated method stub

    System.out.println(
        "*****************Recommendation processing starts******************");
    startTime = System.currentTimeMillis();

    DiscoveryStepAbstract cbCF = new ContentBasedCF(this.props, this.es,
        this.spark);
    cbCF.execute();

    DiscoveryStepAbstract sbCF = new sessionBasedCF(this.props, this.es,
        this.spark);
    sbCF.execute();

    DiscoveryStepAbstract tbCF = new TopicBasedCF(this.props, this.es,
        this.spark);
    tbCF.execute();

    endTime = System.currentTimeMillis();
    System.out.println(
        "*****************Recommendation processing ends******************Took "
            + (endTime - startTime) / 1000);
  }

  @Override
  public void output() {
    // TODO Auto-generated method stub

  }

}
