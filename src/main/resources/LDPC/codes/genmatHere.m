% =========================================================================
% Title       : Generate LDPC Encoder Matrix
% File        : genmat.m
% -------------------------------------------------------------------------
% Description :
%   Creates LDPC generator matrix G and parity check matrix H from a
%   LDPC matrix prototype as described in IEEE 802.11n D2.0 
% ------------------------------------------------------------------------- 
% Revisions   :
%   Date       Version  Author  Description
%   20-may-11  1.3      studer  cleanup for reproducible research
%   02-jul-07  1.1      studer  modified and extended
%   05-oct-06  1.0      studer  file created
% -------------------------------------------------------------------------
%   (C) 2006-2011 Communication Theory Group                      
%   ETH Zurich, 8092 Zurich, Switzerland                               
%   Author: Dr. Christoph Studer (e-mail: studer@rice.edu)     
% =========================================================================


function LDPC = genmatHere(LDPC)

  tic
  disp(sprintf('\n*** %s\n',LDPC.name))       
  
  % == parse propotype matrix and generate H (parity check matrix)
  disp('Create check matrix H ...');
  [N,M] = size(LDPC.H_prot);
  LDPC.H = [];
  for n=1:N
    row = [];
    for m=1:M
      content = char(LDPC.H_prot(n,m));
      if content=='-'
        row = [ row zeros(LDPC.Z) ];
      else
        row = [ row eyeperm(LDPC.Z,str2num(content)) ];
      end
    end
    LDPC.H = [ LDPC.H ; row ];
  end                
  [LDPC.par_bits,LDPC.tot_bits] = size(LDPC.H);
  LDPC.inf_bits = LDPC.tot_bits - LDPC.par_bits;
  LDPC.rate = LDPC.inf_bits/LDPC.tot_bits;       
    
  % == compute parity check matrix 
  disp('Compute generator matrix G ...');
  LDPC.H = gf(LDPC.H,1);
  H1 = LDPC.H(:,1:LDPC.inf_bits);
  H2 = LDPC.H(:,LDPC.inf_bits+1:end);    
  LDPC.P = (inv(H2)*(-H1))';
   
  % -- construct parity check matrix
  LDPC.G = [ gf(eye(LDPC.inf_bits),1) LDPC.P ];
    
  % -- store LDPC construct
  disp('Storing LDPC data to disc ...');
  save(LDPC.name,'LDPC');
  
  toc
return

% -- generate permutation matrix
function PZ = eyeperm(Z,p)
  p = mod(p,Z);
  EZ = eye(Z);
  PZ = [ EZ(:,Z-p+1:Z) EZ(:,1:Z-p) ];
return

