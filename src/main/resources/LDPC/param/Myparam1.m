function Myparam1(RunID) 

  % == LDPC SETTINGS ====================================

  TxRx.Sim.name = 'MY_Gallager';
  TxRx.Sim.nr_of_channels = 100; % 1k for good results, 10k for accurate results
  TxRx.Sim.SNR_dB_list = [0:1:8];
  TxRx.Decoder.LDPC.Scheduling = 'Layered'; % 'Layered' and 'Flooding'
  TxRx.Decoder.LDPC.Type = 'MPA'; % 'MPA' and 'SPA' (optimal)
  TxRx.Decoder.LDPC.Iterations = 10;  
  %load('codes/LDPC_11nD2_648b_R12.mat'); % load code
  %LDPC = MyGallager();
  LDPC = LDPC_11nD2_648b_R12();
  
  % == EXECUTE SIMULATION ===============================  
  
  sim_LDPC(RunID,TxRx,LDPC); 
  
return
  
