function ERR_LDPC_648b_R12_LAYERED_OMS_I5(RunID) 

  % == LDPC SETTINGS ====================================

  TxRx.Sim.name = 'ERR_LDPC_648b_R12_LAYERED_OMS_I5';
  TxRx.Sim.nr_of_channels = 1000; % 1k for good results, 10k for accurate results
  TxRx.Sim.SNR_dB_list = [0:1:8];
  TxRx.Decoder.LDPC.Scheduling = 'Layered'; % 'Layered' and 'Flooding'
  TxRx.Decoder.LDPC.Type = 'OMS'; % 'MPA' and 'SPA' (optimal)
  TxRx.Decoder.LDPC.Iterations = 5;  
  load('codes/LDPC_11nD2_648b_R12.mat'); % load code
  %disp(LDPC);
  
  % == EXECUTE SIMULATION ===============================  
  
  %sim_LDPC(RunID,TxRx,LDPC); 
  
return
  
