% =========================================================================
% Title       : Simulator for Quasi-Cyclic LDPC codes
% File        : sim_LDPC.m
% -------------------------------------------------------------------------
% Description :
%   This file performs the main Monte-Carlo simulation procedure.
%   Encodes LDPC codes described by the codes found in the codes/ folder
%   transmits bits over an AWGN channel and calls the decoding algorithm.
% ------------------------------------------------------------------------- 
% Revisions   :
%   Date       Version  Author  Description
%   20-may-11  1.3      studer  cleanup for reproducible research
%   04-jul-07  1.2      studer  multiple bug fixes
%   02-jul-07  1.1      studer  modularized & improved version
%   05-oct-06  1.0      studer  initial version 
% -------------------------------------------------------------------------
%   (C) 2006-2011 Communication Theory Group                      
%   ETH Zurich, 8092 Zurich, Switzerland                               
%   Author: Dr. Christoph Studer (e-mail: studer@rice.edu)     
% =========================================================================

function sim_LDPC(RunID,TxRx,LDPC) 

  randn('state',RunID)
  rand('state',RunID) 

  % -- initialize
  BER = zeros(1,length(TxRx.Sim.SNR_dB_list)); 
  FER = zeros(1,length(TxRx.Sim.SNR_dB_list));
  log_NumC = zeros(1,length(TxRx.Sim.SNR_dB_list)); 
  log_NumV = zeros(1,length(TxRx.Sim.SNR_dB_list)); 
  
  tic;
  for trial=1:TxRx.Sim.nr_of_channels
        
    % -- draw random bits and map to symbol
    c = gf(round(rand(1,LDPC.inf_bits)),1);
    x = c*LDPC.G; % generate codeword
    noise = randn(1,length(x)); 
    s = sign((x==0)-0.5); % mapping: 1 to -1.0 and 0 to +1.0
  
    for k=1:length(TxRx.Sim.SNR_dB_list)
        
      % -- AWGN channel
      sigma2 = 10^(-TxRx.Sim.SNR_dB_list(k)/10);      
      
      y = s + noise*sqrt(sigma2);
      %count = 0;
      %for i =[1:length(s)]
      %    if (y(1,i) > 0 && s(1,i)<0) ||  (s(1,i) > 0 && y(1,i)<0)
      %        count = count+1;
      %    end
      %end
      %disp(count);
      %y(1:10) = 1;
      %disp(y);
      
      % -- compute LLRs & decode    
      LLR_A2 =  2*y/sigma2;
      disp([y; 2*y/sigma2]);
      
      switch (TxRx.Decoder.LDPC.Scheduling)
        case 'Layered', % layered schedule             
          [bit_output,LLR_D2,NumC,NumV] = decLDPC_layered(TxRx,LDPC,LLR_A2);
        case 'Flooding', % flooding schedule
          [bit_output,LLR_D2,NumC,NumV] = decLDPC_flooding(TxRx,LDPC,LLR_A2);
        otherwise,
          error('Unknown TxRx.Decoder.LDPC.Scheduling method.')  
      end
      log_NumC(k) = log_NumC(k) + NumC;
      log_NumV(k) = log_NumV(k) + NumV;
      
      % -- calculate BER
      ref_output = (c==1);   
      tmp = sum(abs(ref_output-bit_output))/LDPC.inf_bits;
      BER(k) = BER(k) + tmp;  
      FER(k) = FER(k) + (tmp>0);
      
    end
    
    if mod(trial,10)==1
      disp(sprintf('Estimated remaining time is %1.1f minutes.',(toc)/trial*(TxRx.Sim.nr_of_channels-trial)/60));
    end
    
  end

  % -- save results to disk
  Results.TxRx = TxRx;
  Results.LDPC = LDPC;
  Results.BER = BER/trial;
  Results.FER = FER/trial;
  Results.log_NumC = log_NumC/trial;
  Results.log_NumV = log_NumV/trial;  
  Results.FileName = sprintf('results/%s_%d.mat',TxRx.Sim.name,RunID);
  save(Results.FileName,'Results');
  
  % -- generate BER plot
  figure(11); 
  semilogy(TxRx.Sim.SNR_dB_list,Results.BER,'bo-')
  xlabel('SNR [dB]')
  ylabel('BER')
  grid on
  axis([ -1 9 1e-6 1])   
  
return