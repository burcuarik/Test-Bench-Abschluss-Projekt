`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: Saban KOCAL
// 
// Create Date: 05/27/2022 09:52:54 AM
// Design Name: 
// Module Name: get_abs_pos_state_machine
//////////////////////////////////////////////////////////////////////////////////


module get_abs_pos_state_machine(
	input wire clk,
	input wire rst,
	input wire init_state_machine,
	input wire hls_done,
	input wire hls_ready,
	input wire [31:0] axis1_hw_counter,
	input wire [31:0] axis1_set_position_part1,
	input wire [31:0] axis1_set_position_part2,
	input wire [31:0] axis1_counts_per_m,
	input wire [31:0] axis2_hw_counter,
	input wire [31:0] axis2_set_position_part1,
	input wire [31:0] axis2_set_position_part2,
	input wire [31:0] axis2_counts_per_m,
	
	input wire [63:0] selected_axis_hls_calculated_abs_pos,
	
	output reg 		  start_hls_calculations, //start absolute pos. calculations of one of the Axis
	output reg [2:0]  state,
	output reg [31:0] selected_axis_hw_counter,
	output reg [31:0] selected_axis_set_position_part1,
	output reg [31:0] selected_axis_set_position_part2,
	output reg [31:0] selected_axis_counts_per_m,

	output reg [63:0] axis1_hls_calculated_abs_pos,
	output reg [63:0] axis2_hls_calculated_abs_pos
);
	
	reg [2:0] prev_state;
	reg [6:0] wait_execution_counter;

	reg init_state_machine_reg;
	reg hls_done_reg;
	reg hls_ready_reg;	

	integer RESET = 0,  
			INITIAL_STATE   = 1,
			IDLE = 2, 
			CALCULATE_AXIS1_POS = 3,
			CALCULATE_AXIS1_POS_DONE = 4,
			CALCULATE_AXIS2_POS = 5,
			CALCULATE_AXIS2_POS_DONE = 6,
			DONE = 7; 
	
	//Register all input values
	always @ (posedge clk or negedge rst) begin
	if (!rst) begin
		init_state_machine_reg <= 0;
		hls_done_reg <= 0;
		hls_ready_reg <= 0;	
	end    
	else begin       
	   init_state_machine_reg <= init_state_machine;
	   hls_done_reg <= hls_done;
	   hls_ready_reg <= hls_ready;	
	end
	end         
	
    // get absolute position for X and Y Axis State Machine   
    always @ (posedge clk or negedge rst) begin
        if (!rst) begin
            state 								<= RESET;
			prev_state 							<= RESET;
			start_hls_calculations 				<= 0;
			selected_axis_hw_counter 			<= 0;
			selected_axis_set_position_part1 	<= 0;
			selected_axis_set_position_part2 	<= 0;
			selected_axis_counts_per_m 			<= 0;			
        end
        else begin            			
			prev_state <= state;
			start_hls_calculations <= 0;
			
			// state transition                                                                                 
			case (state)
				RESET:
				begin
				   // After releasing reset we run INITIAL_STATE 
				   // If we want to add some additional settings, then we need to add here as new states!!!
				   state  <= INITIAL_STATE;					   
				end					
				INITIAL_STATE:
				begin
					if(init_state_machine_reg) begin				
						state  <= IDLE;	
					end
				end
				IDLE:
				begin
					//if(hls_ready_reg) begin
						state <= CALCULATE_AXIS1_POS;
					//end
				end      
				CALCULATE_AXIS1_POS:
				begin
				   if(prev_state != state) begin // Only first time we do that!
					   start_hls_calculations 			<= 1;
					   selected_axis_hw_counter 		<= axis1_hw_counter;
  					   selected_axis_set_position_part1 <= axis1_set_position_part1;
					   selected_axis_set_position_part2 <= axis1_set_position_part2;
					   selected_axis_counts_per_m 		<= axis1_counts_per_m;
				   end
				   else begin						
  					    start_hls_calculations <= 0;
						if(hls_done_reg) begin
							state 							<= CALCULATE_AXIS1_POS_DONE; //BIRGUL'u cooook seviyorum, code olarak!
							axis1_hls_calculated_abs_pos 	<= selected_axis_hls_calculated_abs_pos; 
						end
				   end
				end    
				CALCULATE_AXIS1_POS_DONE:
				begin
					//if(hls_ready_reg) begin
						state <= CALCULATE_AXIS2_POS;
					//end
				end                
				CALCULATE_AXIS2_POS:
				begin
				   if(prev_state != state) begin // Only first time we do that!
					   start_hls_calculations 			<= 1;
					   selected_axis_hw_counter 		<= axis2_hw_counter;
  					   selected_axis_set_position_part1 <= axis2_set_position_part1;
					   selected_axis_set_position_part2 <= axis2_set_position_part2;
					   selected_axis_counts_per_m 		<= axis2_counts_per_m;
				   end
				   else begin						
  					    start_hls_calculations <= 0;
						if(hls_done_reg) begin
							state 							<= CALCULATE_AXIS2_POS_DONE; //BIRGUL'u cooook seviyorum, code olarak!
							axis2_hls_calculated_abs_pos 	<= selected_axis_hls_calculated_abs_pos;
						end
				   end
				end  
				CALCULATE_AXIS2_POS_DONE:
				begin
				   // Just to follow we were in this state once upon a time!
				   state  <= DONE;
				end						
				DONE:
				begin
				   // Just to follow we were in this state once upon a time!
				   state  <= INITIAL_STATE;
				end						
				default :                                                                                         
				begin                                                                                           
					state  <= INITIAL_STATE;                                                              
				end      	           
			endcase
		end
    end    
	
	
endmodule