% =========================================================================
% Title       : LDPC code decoder with flooding schedule
% File        : decLDPC_flooding.m
% -------------------------------------------------------------------------
% Description :
%   LDPC decoder with standard 'flooding' message passing
%   according to Forney, Loeliger et al. Note that this LDPC decoder
%   requires LLRs defined according to L=log(Pr[x=0]/Pr[x=1]).
% ------------------------------------------------------------------------- 
% Revisions   :
%   Date       Version  Author  Description
%   20-may-11  1.2      studer  cleanup for reproducible research
%   04-jul-07  1.1      studer  rate-related bug fixed
%   02-jul-07  1.0      studer  file created
% -------------------------------------------------------------------------
%   (C) 2006-2011 Communication Theory Group                      
%   ETH Zurich, 8092 Zurich, Switzerland                               
%   Author: Dr. Christoph Studer (e-mail: studer@rice.edu)     
% =========================================================================

function [bit_output,LLR_D2,NumC,NumV] = decLDPC_flooding(TxRx,LDPC,LLR_A2)

  % -- initializations
  numOfEntries = sum(sum(LDPC.H==1));
  M = spalloc(LDPC.inf_bits,LDPC.tot_bits,numOfEntries); % msg matrix
  LLR_D2 = zeros(1,LDPC.tot_bits);
  bit_output = zeros(1,LDPC.inf_bits);
  NumC = 0; % number of computed check nodes
  NumV = 0; % number of computed variable nodes
  
  % -- BEGIN loop over LDPC-internal iterations
  for iter=1:TxRx.Decoder.LDPC.Iterations
  
    % == equality check computation
    for i=1:LDPC.tot_bits                     
      idx = find(LDPC.H(:,i)==1); % slow
      curInput = full(M(idx,i)); % get inputs [slow]   
      % -- compute outputs
      curSum = sum(curInput);
      tmpSum = LLR_A2(i)+curSum;
      LLR_D2(i) = tmpSum; % total probability
      % -- over each entry the current column                
      curOutput = zeros(length(curInput),1); 
      for j=1:length(curInput)
        curOutput(j,1) =  tmpSum-curInput(j,1);
        % -- count the number of computed variable nodes
        NumV = NumV + 1;
      end
      % -- update column
      M(idx,i) = curOutput;    
    end
               
    % == parity check computation
    for j=1:LDPC.par_bits                
      idx = find(LDPC.H(j,:)==1); % slow
      curInput = full(M(j,idx)); % get inputs [slow]
      % -- for each row compute parity checks
      curOutput = zeros(1,length(curInput));
      for i=1:length(curInput)
        tmpInput = curInput;
        tmpInput(i) = []; % delete entry
        switch (TxRx.Decoder.LDPC.Type) 
          case 'SPA', % -- sum-product algorithm                
            % -- perform computations in log-domain                
            psi = sum(-log(1e-300+tanh(abs(tmpInput)*0.5)));
            sgn = prod(sign(tmpInput));                
            curOutput(1,i) = sgn*(-log(1e-300+tanh(psi*0.5)));
          case 'MPA', % -- max-log approximation (max-product algorithm)
            curOutput(1,i) = prod(mysign(tmpInput))*min(abs(tmpInput));
          otherwise,
            error('Unknown TxRx.Decoder.LDPC.Type.')
        end  
        % -- count the number of computed check nodes (not in last iteration)
        if iter<TxRx.Decoder.LDPC.Iterations
          NumC = NumC + 1;
        end
      end
      M(j,idx) = curOutput;
    end

  end % -- END iter
  
  % -- compute binary-valued estimates
  bit_output = 0.5*(1-mysign(LLR_D2(1:LDPC.inf_bits)));
      
return

function s = mysign(inp)
  s = 2*(inp>0)-1; 
return